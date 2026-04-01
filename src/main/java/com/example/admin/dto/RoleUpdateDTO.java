package com.example.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色更新请求 DTO
 */
@Data
@Schema(description = "角色更新请求")
public class RoleUpdateDTO {

    @Size(min = 2, max = 64, message = "角色编码长度为2-64个字符")
    @Schema(description = "角色编码")
    private String roleCode;

    @Size(min = 2, max = 64, message = "角色名称长度为2-64个字符")
    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Size(max = 255, message = "备注长度不能超过255个字符")
    @Schema(description = "备注")
    private String remark;
}