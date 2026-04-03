package com.example.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.common.constant.EntityConstants;
import com.example.admin.dto.CancelOrderDTO;
import com.example.admin.dto.CreateOrderDTO;
import com.example.admin.dto.OrderItemDTO;
import com.example.admin.dto.OrderQueryDTO;
import com.example.admin.entity.Order;
import com.example.admin.entity.OrderItem;
import com.example.admin.entity.Product;
import com.example.admin.entity.ProductStock;
import com.example.admin.enums.OrderStatusEnum;
import com.example.admin.exception.BusinessException;
import com.example.admin.mapper.OrderMapper;
import com.example.admin.service.OrderItemService;
import com.example.admin.service.OrderService;
import com.example.admin.service.ProductService;
import com.example.admin.service.ProductStockService;
import com.example.admin.utils.RedisUtil;
import com.example.admin.vo.CreateOrderVO;
import com.example.admin.vo.OrderDetailVO;
import com.example.admin.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private static final String IDEMPOTENT_KEY_PREFIX = "order:idempotent:";
    private static final String IDEMPOTENT_PROCESSING = "PROCESSING";
    private static final long IDEMPOTENT_TTL = 3600; // 1小时
    private static final int ORDER_NO_RETRY_TIMES = 3;

    private final ProductService productService;
    private final ProductStockService productStockService;
    private final OrderItemService orderItemService;
    private final RedisUtil redisUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateOrderVO create(CreateOrderDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        String idempotentKey = IDEMPOTENT_KEY_PREFIX + dto.getIdempotentKey();

        // 1. 幂等校验（使用 SETNX 原子操作）
        Boolean acquired = redisUtil.setIfAbsent(idempotentKey, IDEMPOTENT_PROCESSING, IDEMPOTENT_TTL);
        if (acquired == null || !acquired) {
            // 检查是结果还是处理中
            Object cached = redisUtil.get(idempotentKey);
            if (cached instanceof CreateOrderVO) {
                log.info("幂等命中，直接返回上次结果，userId={}, idempotentKey={}", userId, dto.getIdempotentKey());
                return (CreateOrderVO) cached;
            }
            throw new BusinessException("请求正在处理中，请稍后查询订单状态");
        }

        try {
            // 2. 合并同一商品的购买数量
            List<OrderItemDTO> mergedItems = mergeOrderItems(dto.getItems());

            // 3. 校验并收集商品信息
            List<Product> products = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (OrderItemDTO item : mergedItems) {
                Product product = productService.validateProduct(item.getProductId());
                products.add(product);

                // 计算小计
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }

            // 4. 预扣库存（乐观锁 + 重试）
            for (int i = 0; i < mergedItems.size(); i++) {
                OrderItemDTO item = mergedItems.get(i);
                boolean success = deductStockWithRetry(item.getProductId(), item.getQuantity());
                if (!success) {
                    // 乐观锁失败，释放已扣减的库存
                    for (int j = 0; j < i; j++) {
                        OrderItemDTO rollbackItem = mergedItems.get(j);
                        productStockService.releaseStock(rollbackItem.getProductId(), rollbackItem.getQuantity());
                    }
                    // 清理幂等键，允许用户重试
                    Object cached = redisUtil.get(idempotentKey);
                    if (IDEMPOTENT_PROCESSING.equals(cached)) {
                        redisUtil.del(idempotentKey);
                    }
                    throw new BusinessException("库存扣减失败，请稍后重试");
                }
            }

            // 5. 生成订单号（带唯一索引冲突重试）
            String orderNo = null;
            Order order = null;
            for (int retry = 0; retry < ORDER_NO_RETRY_TIMES; retry++) {
                try {
                    orderNo = generateOrderNo();
                    // 6. 创建订单
                    order = new Order();
                    order.setOrderNo(orderNo);
                    order.setUserId(userId);
                    order.setTotalAmount(totalAmount);
                    order.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
                    order.setAddress(dto.getAddress());
                    save(order);
                    break;
                } catch (Exception e) {
                    if (retry == ORDER_NO_RETRY_TIMES - 1) {
                        throw new BusinessException("订单创建失败，请稍后重试");
                    }
                    log.warn("订单号冲突，重试生成，retry={}", retry + 1);
                }
            }

            // 7. 创建订单明细
            List<OrderItem> orderItems = new ArrayList<>();
            for (int i = 0; i < mergedItems.size(); i++) {
                OrderItemDTO itemDTO = mergedItems.get(i);
                Product product = products.get(i);

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getProductName());
                orderItem.setPrice(product.getPrice());
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
                orderItems.add(orderItem);
            }
            orderItemService.saveBatch(orderItems);

            // 8. 构造返回结果并缓存幂等结果
            CreateOrderVO result = CreateOrderVO.builder()
                    .orderId(order.getId())
                    .orderNo(order.getOrderNo())
                    .totalAmount(order.getTotalAmount())
                    .build();

            redisUtil.set(idempotentKey, result, IDEMPOTENT_TTL);

            log.info("订单创建成功，userId={}, orderId={}, orderNo={}", userId, order.getId(), orderNo);

            return result;
        } catch (Exception e) {
            // 异常时清除 PROCESSING 标记，允许重试
            Object cached = redisUtil.get(idempotentKey);
            if (IDEMPOTENT_PROCESSING.equals(cached)) {
                redisUtil.del(idempotentKey);
            }
            throw e;
        }
    }

    @Override
    public IPage<OrderVO> page(OrderQueryDTO queryDTO) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 限制最大页大小，防止大查询拖垮数据库
        int size = Math.min(queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10, EntityConstants.MAX_PAGE_SIZE);
        int pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;

        Page<Order> page = new Page<>(pageNum, size);

        // 构建查询条件（只能查询自己的订单）
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);

        // 订单号模糊查询
        if (queryDTO.getOrderNo() != null && !queryDTO.getOrderNo().isBlank()) {
            wrapper.like(Order::getOrderNo, queryDTO.getOrderNo());
        }

        // 状态精确查询
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Order::getStatus, queryDTO.getStatus());
        }

        // 时间范围查询
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(Order::getCreatedAt, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(Order::getCreatedAt, queryDTO.getEndTime());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(Order::getCreatedAt);

        Page<Order> orderPage = page(page, wrapper);
        return orderPage.convert(this::convertToOrderVO);
    }

    @Override
    public OrderDetailVO detail(Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询订单
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 校验订单归属
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该订单");
        }

        // 查询订单明细
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemService.list(itemWrapper);

        // 转换为 VO
        return convertToOrderDetailVO(order, items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(CancelOrderDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询订单
        Order order = getById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 校验订单归属
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权取消该订单");
        }

        // 校验订单状态（只有待支付状态可以取消）
        OrderStatusEnum currentStatus = OrderStatusEnum.getByCode(order.getStatus());
        if (currentStatus != OrderStatusEnum.PENDING_PAYMENT) {
            throw new BusinessException("当前订单状态不支持取消");
        }

        // 查询订单明细
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, dto.getOrderId());
        List<OrderItem> items = orderItemService.list(itemWrapper);

        // 释放库存
        for (OrderItem item : items) {
            boolean success = productStockService.releaseStock(item.getProductId(), item.getQuantity());
            if (!success) {
                throw new BusinessException("库存释放失败，请稍后重试");
            }
        }

        // 更新订单状态为已取消
        order.setStatus(OrderStatusEnum.CANCELLED.getCode());
        order.setCancelReason(dto.getCancelReason());
        updateById(order);

        log.info("订单取消成功，userId={}, orderId={}", userId, dto.getOrderId());
    }

    /**
     * 合并同一商品的购买数量
     */
    private List<OrderItemDTO> mergeOrderItems(List<OrderItemDTO> items) {
        Map<Long, OrderItemDTO> mergedMap = new HashMap<>();
        for (OrderItemDTO item : items) {
            if (mergedMap.containsKey(item.getProductId())) {
                OrderItemDTO existing = mergedMap.get(item.getProductId());
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
            } else {
                mergedMap.put(item.getProductId(), item);
            }
        }
        return new ArrayList<>(mergedMap.values());
    }

    /**
     * 库存扣减（带重试）
     */
    private boolean deductStockWithRetry(Long productId, Integer quantity) {
        for (int retry = 0; retry < EntityConstants.MAX_DEDUCT_RETRY_TIMES; retry++) {
            boolean success = productStockService.deductStock(productId, quantity);
            if (success) {
                return true;
            }
            if (retry < EntityConstants.MAX_DEDUCT_RETRY_TIMES - 1) {
                try {
                    Thread.sleep(EntityConstants.DEDUCT_RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return false;
    }

    /**
     * Order 转换为 OrderVO
     */
    private OrderVO convertToOrderVO(Order order) {
        OrderStatusEnum statusEnum = OrderStatusEnum.getByCode(order.getStatus());
        return OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .statusDesc(statusEnum != null ? statusEnum.getDesc() : "未知状态")
                .address(order.getAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }

    /**
     * Order + OrderItem 转换为 OrderDetailVO
     */
    private OrderDetailVO convertToOrderDetailVO(Order order, List<OrderItem> items) {
        OrderStatusEnum statusEnum = OrderStatusEnum.getByCode(order.getStatus());

        List<OrderDetailVO.OrderItemVO> itemVOs = items.stream()
                .map(item -> OrderDetailVO.OrderItemVO.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .toList();

        return OrderDetailVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .statusDesc(statusEnum != null ? statusEnum.getDesc() : "未知状态")
                .cancelReason(order.getCancelReason())
                .address(order.getAddress())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemVOs)
                .build();
    }

    /**
     * 生成订单号：yyyyMMddHHmmss + 6位随机数
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomNum = 100000 + ThreadLocalRandom.current().nextInt(900000);
        return timestamp + randomNum;
    }

}
