package com.example.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色分配菜单请求 DTO
 */
@Data
@Schema(description = "角色分配菜单请求")
public class RoleAssignMenuDTO {

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roleId;

    @Schema(description = "菜单ID列表")
    private List<Long> menuIds;
}