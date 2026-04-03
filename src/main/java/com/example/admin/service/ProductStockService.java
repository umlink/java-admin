package com.example.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.admin.entity.ProductStock;

/**
 * 商品库存服务接口
 */
public interface ProductStockService extends IService<ProductStock> {

    /**
     * 根据商品ID查询库存
     *
     * @param productId 商品ID
     * @return 库存信息
     */
    ProductStock getByProductId(Long productId);

    /**
     * 预扣库存（乐观锁）
     *
     * @param productId 商品ID
     * @param quantity  扣减数量
     * @return 是否扣减成功
     */
    boolean deductStock(Long productId, Integer quantity);

    /**
     * 释放库存（取消订单时调用）
     *
     * @param productId 商品ID
     * @param quantity  释放数量
     * @return 是否释放成功
     */
    boolean releaseStock(Long productId, Integer quantity);

}
