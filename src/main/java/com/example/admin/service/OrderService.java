package com.example.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.admin.dto.CancelOrderDTO;
import com.example.admin.dto.CreateOrderDTO;
import com.example.admin.dto.OrderQueryDTO;
import com.example.admin.entity.Order;
import com.example.admin.vo.CreateOrderVO;
import com.example.admin.vo.OrderDetailVO;
import com.example.admin.vo.OrderVO;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     *
     * @param dto 创建订单请求
     * @return 创建订单响应
     */
    CreateOrderVO create(CreateOrderDTO dto);

    /**
     * 分页查询订单列表（只能查询自己的订单）
     *
     * @param queryDTO 查询条件
     * @return 订单分页列表
     */
    IPage<OrderVO> page(OrderQueryDTO queryDTO);

    /**
     * 查询订单详情（含明细）
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDetailVO detail(Long orderId);

    /**
     * 取消订单
     *
     * @param dto 取消订单请求
     */
    void cancel(CancelOrderDTO dto);

}
