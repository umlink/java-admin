package com.example.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建订单请求DTO
 */
@Data
@Schema(description = "创建订单请求")
public class CreateOrderDTO {

    @NotEmpty(message = "商品列表不能为空")
    @Valid
    @Schema(description = "商品列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemDTO> items;

    @NotBlank(message = "收货地址不能为空")
    @Size(max = 512, message = "收货地址长度不能超过512个字符")
    @Schema(description = "收货地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    @NotBlank(message = "幂等键不能为空")
    @Schema(description = "幂等键（前端生成的UUID）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String idempotentKey;

}
