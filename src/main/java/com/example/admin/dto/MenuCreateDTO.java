package com.example.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 菜单创建请求 DTO
 */
@Data
@Schema(description = "菜单创建请求")
public class MenuCreateDTO {

    @NotNull(message = "父菜单ID不能为空")
    @Schema(description = "父菜单ID，顶级菜单为0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long parentId;

    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 1, max = 64, message = "菜单名称长度为1-64个字符")
    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String menuName;

    @NotNull(message = "菜单类型不能为空")
    @Schema(description = "菜单类型：1-目录，2-菜单，3-按钮", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer menuType;

    @Size(max = 128, message = "菜单路径长度不能超过128个字符")
    @Schema(description = "菜单路径")
    private String path;

    @Size(max = 128, message = "组件路径长度不能超过128个字符")
    @Schema(description = "组件路径")
    private String component;

    @Size(max = 128, message = "权限编码长度不能超过128个字符")
    @Schema(description = "权限编码")
    private String permissionCode;

    @Schema(description = "排序号")
    private Integer sortNum;
}