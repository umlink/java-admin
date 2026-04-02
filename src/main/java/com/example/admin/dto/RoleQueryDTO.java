package com.example.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色查询条件 DTO
 */
@Data
@Schema(description = "角色查询条件")
public class RoleQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量，最大100", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "角色名称（模糊查询）")
    private String roleName;

    @Schema(description = "状态（1启用 0禁用）")
    private Integer status;
}
