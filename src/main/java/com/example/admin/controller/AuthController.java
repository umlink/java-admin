package com.example.admin.controller;

import com.example.admin.annotation.OperateLog;
import com.example.admin.common.api.Result;
import com.example.admin.dto.LoginDTO;
import com.example.admin.service.SysUserService;
import com.example.admin.vo.LoginVO;
import com.example.admin.vo.PermissionVO;
import com.example.admin.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "登录登出相关接口")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;

    /**
     * 登录
     */
    @PostMapping("/login")
    @Operation(summary = "登录", description = "用户登录")
    @OperateLog(module = "认证", description = "用户登录", recordResult = false)
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(sysUserService.login(dto));
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    @Operation(summary = "登出", description = "用户登出")
    @OperateLog(module = "认证", description = "用户登出", recordParams = false)
    public Result<Void> logout() {
        sysUserService.logout();
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户信息")
    public Result<UserVO> getCurrentUser() {
        return Result.success(sysUserService.getCurrentUser());
    }

    /**
     * 获取当前用户权限聚合信息
     */
    @GetMapping("/permission")
    @Operation(summary = "获取权限信息", description = "获取当前用户权限聚合信息（角色、菜单、权限码）")
    public Result<PermissionVO> getPermission() {
        return Result.success(sysUserService.getPermissionInfo());
    }
}