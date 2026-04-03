package com.example.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * 权限聚合视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "权限聚合视图对象")
public class PermissionVO {

    @Schema(description = "用户信息")
    private UserVO user;

    @Schema(description = "角色列表")
    private List<RoleVO> roles;

    @Schema(description = "菜单树")
    private List<MenuVO> menus;

    @Schema(description = "权限码集合")
    private Set<String> permissions;
}
