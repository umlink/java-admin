package com.example.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.admin.annotation.OperateLog;
import com.example.admin.annotation.RepeatSubmit;
import com.example.admin.common.api.Result;
import com.example.admin.common.constant.PermissionConstants;
import com.example.admin.dto.RoleAssignMenuDTO;
import com.example.admin.dto.RoleCreateDTO;
import com.example.admin.dto.RoleQueryDTO;
import com.example.admin.dto.RoleUpdateDTO;
import com.example.admin.service.SysRoleService;
import com.example.admin.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/role")
@Tag(name = "角色管理", description = "角色相关接口")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    /**
     * 获取角色列表（分页，查询参数超过1个使用POST）
     */
    @PostMapping("/list")
    @SaCheckPermission(PermissionConstants.ROLE_LIST)
    @Operation(summary = "获取角色列表", description = "分页获取角色列表，支持按角色名称和状态筛选")
    @OperateLog(module = "角色管理", description = "查询角色列表", recordResult = false)
    public Result<IPage<RoleVO>> list(@RequestBody RoleQueryDTO queryDTO) {
        return Result.success(sysRoleService.getRolePage(queryDTO));
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.ROLE_LIST)
    @Operation(summary = "获取角色详情", description = "根据ID获取角色详情")
    @OperateLog(module = "角色管理", description = "查询角色详情", recordResult = false)
    public Result<RoleVO> getById(@Parameter(description = "角色ID") @PathVariable Long id) {
        return Result.success(sysRoleService.getRoleById(id));
    }

    /**
     * 新增角色
     */
    @PostMapping
    @SaCheckPermission(PermissionConstants.ROLE_CREATE)
    @Operation(summary = "新增角色", description = "创建新角色")
    @RepeatSubmit
    @OperateLog(module = "角色管理", description = "新增角色")
    public Result<RoleVO> save(@Valid @RequestBody RoleCreateDTO dto) {
        return Result.success(sysRoleService.createRole(dto));
    }

    /**
     * 修改角色
     */
    @PutMapping("/{id}")
    @SaCheckPermission(PermissionConstants.ROLE_UPDATE)
    @Operation(summary = "修改角色", description = "更新角色信息")
    @RepeatSubmit
    @OperateLog(module = "角色管理", description = "修改角色")
    public Result<RoleVO> update(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Valid @RequestBody RoleUpdateDTO dto) {
        return Result.success(sysRoleService.updateRole(id, dto));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission(PermissionConstants.ROLE_DELETE)
    @Operation(summary = "删除角色", description = "根据ID删除角色")
    @RepeatSubmit
    @OperateLog(module = "角色管理", description = "删除角色")
    public Result<Void> delete(@Parameter(description = "角色ID") @PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 获取角色拥有的菜单ID列表
     */
    @GetMapping("/{id}/menus")
    @SaCheckPermission(PermissionConstants.ROLE_LIST)
    @Operation(summary = "获取角色菜单", description = "获取角色拥有的菜单ID列表")
    @OperateLog(module = "角色管理", description = "查询角色菜单", recordResult = false)
    public Result<List<Long>> getRoleMenus(@Parameter(description = "角色ID") @PathVariable Long id) {
        return Result.success(sysRoleService.getRoleMenuIds(id));
    }

    /**
     * 给角色分配菜单
     */
    @PostMapping("/assign-menus")
    @SaCheckPermission(PermissionConstants.ROLE_ASSIGN_MENU)
    @Operation(summary = "分配菜单", description = "给角色分配菜单")
    @RepeatSubmit
    @OperateLog(module = "角色管理", description = "分配角色菜单")
    public Result<Void> assignMenus(@Valid @RequestBody RoleAssignMenuDTO dto) {
        sysRoleService.assignMenus(dto);
        return Result.success();
    }
}
