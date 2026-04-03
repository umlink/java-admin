package com.example.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 操作日志查询DTO
 */
@Data
@Schema(description = "操作日志查询请求")
public class SysOperateLogQueryDTO {

    @Schema(description = "页码，从1开始", defaultValue = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", defaultValue = "10")
    @Min(value = 1, message = "每页条数不能小于1")
    private Integer pageSize = 10;

    @Schema(description = "操作人用户名（模糊查询）")
    private String username;

    @Schema(description = "操作模块")
    private String module;

    @Schema(description = "操作状态：0-失败，1-成功")
    private Integer status;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;

}
