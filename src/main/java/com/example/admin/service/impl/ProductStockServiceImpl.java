package com.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.entity.ProductStock;
import com.example.admin.exception.BusinessException;
import com.example.admin.mapper.ProductStockMapper;
import com.example.admin.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 商品库存服务实现类
 */
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
        int affected = productStockMapper.releaseStock(productId, quantity);
        return affected > 0;
    }

}
