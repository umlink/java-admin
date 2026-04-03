package com.example.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.admin.entity.ProductStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品库存Mapper
 */
@Mapper
public interface ProductStockMapper extends BaseMapper<ProductStock> {

    /**
     * 预扣库存（乐观锁）
     * 只有当可用库存 >= 扣减数量且版本号匹配时才扣减成功
     *
     * @param productId 商品ID
     * @param quantity  扣减数量
     * @param version   当前版本号
     * @return 影响行数
     */
    @Update("UPDATE product_stock " +
            "SET available_stock = available_stock - #{quantity}, " +
            "    locked_stock = locked_stock + #{quantity}, " +
            "    version = version + 1 " +
            "WHERE product_id = #{productId} " +
            "  AND available_stock >= #{quantity} " +
            "  AND version = #{version}")
    int deductStock(@Param("productId") Long productId,
                    @Param("quantity") Integer quantity,
                    @Param("version") Integer version);

    /**
     * 释放库存（取消订单时调用，带乐观锁）
     *
     * @param productId 商品ID
     * @param quantity  释放数量
     * @param version   当前版本号
     * @return 影响行数
     */
    @Update("UPDATE product_stock " +
            "SET available_stock = available_stock + #{quantity}, " +
            "    locked_stock = locked_stock - #{quantity}, " +
            "    version = version + 1 " +
            "WHERE product_id = #{productId} " +
            "  AND locked_stock >= #{quantity} " +
            "  AND version = #{version}")
    int releaseStock(@Param("productId") Long productId,
                     @Param("quantity") Integer quantity,
                     @Param("version") Integer version);

}
