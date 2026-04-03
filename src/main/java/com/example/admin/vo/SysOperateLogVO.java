package com.example.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志视图对象
 */
@Data
@Schema(description = "操作日志视图对象")
public class SysOperateLogVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "链路追踪ID")
    private String traceId;

    @Schema(description = "操作人ID")
    private Long userId;

    @Schema(description = "操作人用户名")
    private String username;

    @Schema(description = "操作模块")
    private String module;

    @Schema(description = "操作描述")
    private String description;

    @Schema(description = "请求方式")
    private String requestMethod;

    @Schema(description = "请求URI")
    private String requestUri;

    @Schema(description = "请求参数")
    private String requestParams;

    @Schema(description = "响应结果")
    private String responseResult;

    @Schema(description = "执行时长（毫秒）")
    private Long duration;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "用户代理")
    private String userAgent;

    @Schema(description = "操作状态：0-失败，1-成功")
    private Integer status;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

}
