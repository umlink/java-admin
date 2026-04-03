package com.example.admin.dto;

import com.example.admin.common.constant.EntityConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单商品项DTO
 */
@Data
@Schema(description = "订单商品项")
public class OrderItemDTO {

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    @Max(value = EntityConstants.MAX_PURCHASE_QUANTITY, message = "单次购买数量不能超过" + EntityConstants.MAX_PURCHASE_QUANTITY)
    @Schema(description = "购买数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

}
