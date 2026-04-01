package com.example.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.admin.common.api.Result;
import com.example.admin.dto.UserAssignRoleDTO;
import com.example.admin.dto.UserCreateDTO;
import com.example.admin.dto.UserUpdateDTO;
import com.example.admin.service.SysUserService;
import com.example.admin.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户相关接口")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 获取用户列表（分页）
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户列表", description = "分页获取用户列表")
    public Result<IPage<UserVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(sysUserService.getUserPage(new Page<>(page, size)));
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详情")
    public Result<UserVO> getById(@Parameter(description = "用户ID") @PathVariable Long id) {
        return Result.success(sysUserService.getUserById(id));
    }

    /**
     * 新增用户
     */
    @PostMapping
    @Operation(summary = "新增用户", description = "创建新用户")
    public Result<UserVO> save(@Valid @RequestBody UserCreateDTO dto) {
        return Result.success(sysUserService.createUser(dto));
    }

    /**
     * 修改用户
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改用户", description = "更新用户信息")
    public Result<UserVO> update(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {
        return Result.success(sysUserService.updateUser(id, dto));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据ID删除用户（逻辑删除）")
    public Result<Void> delete(@Parameter(description = "用户ID") @PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success();
    }

    /**
     * 获取用户拥有的角色ID列表
     */
    @GetMapping("/{id}/roles")
    @Operation(summary = "获取用户角色", description = "获取用户拥有的角色ID列表")
    public Result<List<Long>> getUserRoles(@Parameter(description = "用户ID") @PathVariable Long id) {
        return Result.success(sysUserService.getUserRoleIds(id));
    }

    /**
     * 给用户分配角色
     */
    @PostMapping("/assign-roles")
    @Operation(summary = "分配角色", description = "给用户分配角色")
    public Result<Void> assignRoles(@Valid @RequestBody UserAssignRoleDTO dto) {
        sysUserService.assignRoles(dto);
        return Result.success();
    }

}