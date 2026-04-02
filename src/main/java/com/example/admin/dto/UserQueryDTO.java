package com.example.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户查询条件 DTO
 */
@Data
@Schema(description = "用户查询条件")
public class UserQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量，最大100", example = "10")
    private Integer size = 10;

    @Schema(description = "用户名（模糊查询）", example = "admin")
    private String username;

    @Schema(description = "状态（1启用 0禁用）", example = "1")
    private Integer status;
}
