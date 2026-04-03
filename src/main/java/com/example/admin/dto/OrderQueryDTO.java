package com.example.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单查询条件 DTO
 */
@Data
@Schema(description = "订单查询条件")
public class OrderQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量，最大100", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "订单号（模糊查询）", example = "20240101120000123456")
    private String orderNo;

    @Schema(description = "订单状态：1-待支付，2-已支付，3-已发货，4-已完成，5-已取消", example = "1")
    private Integer status;

    @Schema(description = "创建时间起始", example = "2024-01-01T00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "创建时间结束", example = "2024-12-31T23:59:59")
    private LocalDateTime endTime;

}
