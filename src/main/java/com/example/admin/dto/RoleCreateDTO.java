package com.example.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色创建请求 DTO
 */
@Data
@Schema(description = "角色创建请求")
public class RoleCreateDTO {

    @NotBlank(message = "角色编码不能为空")
    @Size(min = 2, max = 64, message = "角色编码长度为2-64个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "角色编码只能包含字母、数字和下划线")
    @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 64, message = "角色名称长度为2-64个字符")
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;

    @Size(max = 255, message = "备注长度不能超过255个字符")
    @Schema(description = "备注")
    private String remark;
}