package com.example.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.admin.entity.Product;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<Product> {

    /**
     * 根据ID查询商品（带缓存）
     *
     * @param productId 商品ID
     * @return 商品信息
     */
    Product getProductById(Long productId);

    /**
     * 校验商品是否存在且上架
     *
     * @param productId 商品ID
     * @return 商品信息
     */
    Product validateProduct(Long productId);

    /**
     * 更新商品信息并清除缓存
     *
     * @param product 商品信息
     * @return 是否更新成功
     */
    boolean updateProduct(Product product);

    /**
     * 删除商品并清除缓存
     *
     * @param productId 商品ID
     * @return 是否删除成功
     */
    boolean deleteProduct(Long productId);

}
