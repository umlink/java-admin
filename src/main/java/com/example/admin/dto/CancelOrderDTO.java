package com.example.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 取消订单请求 DTO
 */
@Data
@Schema(description = "取消订单请求")
public class CancelOrderDTO {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long orderId;

    @NotBlank(message = "取消原因不能为空")
    @Size(max = 256, message = "取消原因长度不能超过256个字符")
    @Schema(description = "取消原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "不想买了")
    private String cancelReason;

}
