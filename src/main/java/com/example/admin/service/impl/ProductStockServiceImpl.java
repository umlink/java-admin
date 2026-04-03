package com.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.common.constant.EntityConstants;
import com.example.admin.entity.ProductStock;
import com.example.admin.exception.BusinessException;
import com.example.admin.mapper.ProductStockMapper;
import com.example.admin.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 商品库存服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductStockServiceImpl extends ServiceImpl<ProductStockMapper, ProductStock> implements ProductStockService {

    private final ProductStockMapper productStockMapper;

    @Override
    public ProductStock getByProductId(Long productId) {
        LambdaQueryWrapper<ProductStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductStock::getProductId, productId);
        return getOne(wrapper);
    }

    @Override
    public boolean deductStock(Long productId, Integer quantity) {
        ProductStock stock = getByProductId(productId);
        if (stock == null) {
            throw new BusinessException("商品库存不存在：" + productId);
        }
        if (stock.getAvailableStock() < quantity) {
            throw new BusinessException("商品库存不足");
        }
        int affected = productStockMapper.deductStock(productId, quantity, stock.getVersion());
        return affected > 0;
    }

    @Override
    public boolean releaseStock(Long productId, Integer quantity) {
        for (int retry = 0; retry < EntityConstants.MAX_DEDUCT_RETRY_TIMES; retry++) {
            ProductStock stock = getByProductId(productId);
            if (stock == null) {
                throw new BusinessException("商品库存不存在：" + productId);
            }
            int affected = productStockMapper.releaseStock(productId, quantity, stock.getVersion());
            if (affected > 0) {
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
        log.warn("库存释放失败，超过最大重试次数，productId={}, quantity={}", productId, quantity);
        return false;
    }

}
