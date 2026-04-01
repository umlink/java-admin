package com.example.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "菜单视图对象")
public class MenuVO {

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单类型：1-目录，2-菜单，3-按钮")
    private Integer menuType;

    @Schema(description = "菜单路径")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "权限编码")
    private String permissionCode;

    @Schema(description = "排序号")
    private Integer sortNum;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "子菜单")
    private List<MenuVO> children;
}