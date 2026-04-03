package com.example.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.common.constant.EntityConstants;
import com.example.admin.entity.Product;
import com.example.admin.exception.BusinessException;
import com.example.admin.mapper.ProductMapper;
import com.example.admin.service.ProductService;
import com.example.admin.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private static final String PRODUCT_CACHE_KEY = "product:info:";
    private static final long PRODUCT_CACHE_TTL = 300; // 5分钟

    private final RedisUtil redisUtil;

    @Override
    public Product getProductById(Long productId) {
        String cacheKey = PRODUCT_CACHE_KEY + productId;
        Product product = (Product) redisUtil.get(cacheKey);
        if (product == null) {
            product = getById(productId);
            if (product != null) {
                redisUtil.set(cacheKey, product, PRODUCT_CACHE_TTL);
            }
        }
        return product;
    }

    @Override
    public Product validateProduct(Long productId) {
        Product product = getProductById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在：" + productId);
        }
        if (product.getStatus() != EntityConstants.STATUS_ENABLED) {
            throw new BusinessException("商品已下架：" + product.getProductName());
        }
        return product;
    }

    @Override
    public boolean updateProduct(Product product) {
        boolean success = updateById(product);
        if (success) {
            String cacheKey = PRODUCT_CACHE_KEY + product.getId();
            redisUtil.del(cacheKey);
            log.info("商品缓存已清除，productId={}", product.getId());
        }
        return success;
    }

    @Override
    public boolean deleteProduct(Long productId) {
        boolean success = removeById(productId);
        if (success) {
            String cacheKey = PRODUCT_CACHE_KEY + productId;
            redisUtil.del(cacheKey);
            log.info("商品缓存已清除，productId={}", productId);
        }
        return success;
    }

}
