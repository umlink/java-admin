package com.example.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 用户分配角色请求 DTO
 */
@Data
@Schema(description = "用户分配角色请求")
public class UserAssignRoleDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}