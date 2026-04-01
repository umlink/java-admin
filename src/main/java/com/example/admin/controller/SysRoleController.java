package com.example.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.admin.common.api.Result;
import com.example.admin.dto.RoleAssignMenuDTO;
import com.example.admin.dto.RoleCreateDTO;
import com.example.admin.dto.RoleUpdateDTO;
import com.example.admin.entity.SysRole;
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
@RequestMapping("/api/role")
@Tag(name = "角色管理", description = "角色相关接口")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    /**
     * 获取角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取角色列表", description = "分页获取角色列表")
    public Result<IPage<RoleVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(sysRoleService.getRolePage(new Page<>(page, size)));
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情", description = "根据ID获取角色详情")
    public Result<RoleVO> getById(@Parameter(description = "角色ID") @PathVariable Long id) {
        return Result.success(sysRoleService.getRoleById(id));
    }

    /**
     * 新增角色
     */
    @PostMapping
    @Operation(summary = "新增角色", description = "创建新角色")
    public Result<RoleVO> save(@Valid @RequestBody RoleCreateDTO dto) {
        return Result.success(sysRoleService.createRole(dto));
    }

    /**
     * 修改角色
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改角色", description = "更新角色信息")
    public Result<RoleVO> update(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Valid @RequestBody RoleUpdateDTO dto) {
        return Result.success(sysRoleService.updateRole(id, dto));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "根据ID删除角色")
    public Result<Void> delete(@Parameter(description = "角色ID") @PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 获取角色拥有的菜单ID列表
     */
    @GetMapping("/{id}/menus")
    @Operation(summary = "获取角色菜单", description = "获取角色拥有的菜单ID列表")
    public Result<List<Long>> getRoleMenus(@Parameter(description = "角色ID") @PathVariable Long id) {
        return Result.success(sysRoleService.getRoleMenuIds(id));
    }

    /**
     * 给角色分配菜单
     */
    @PostMapping("/assign-menus")
    @Operation(summary = "分配菜单", description = "给角色分配菜单")
    public Result<Void> assignMenus(@Valid @RequestBody RoleAssignMenuDTO dto) {
        sysRoleService.assignMenus(dto);
        return Result.success();
    }
}
