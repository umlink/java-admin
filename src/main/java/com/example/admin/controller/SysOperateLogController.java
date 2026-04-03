package com.example.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.admin.common.api.Result;
import com.example.admin.common.constant.PermissionConstants;
import com.example.admin.dto.SysOperateLogQueryDTO;
import com.example.admin.service.SysOperateLogService;
import com.example.admin.vo.SysOperateLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/operate-log")
@Tag(name = "操作日志管理", description = "操作日志相关接口")
@RequiredArgsConstructor
public class SysOperateLogController {

    private final SysOperateLogService sysOperateLogService;

    /**
     * 获取操作日志列表（分页）
     */
    @PostMapping("/list")
    @SaCheckPermission(PermissionConstants.OPERATE_LOG_LIST)
    @Operation(summary = "获取操作日志列表", description = "分页获取操作日志列表，支持按用户名、模块、状态、时间范围筛选")
    public Result<IPage<SysOperateLogVO>> list(@Valid @RequestBody SysOperateLogQueryDTO queryDTO) {
        return Result.success(sysOperateLogService.getOperateLogPage(queryDTO));
    }

    /**
     * 获取操作日志详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.OPERATE_LOG_DETAIL)
    @Operation(summary = "获取操作日志详情", description = "根据ID获取操作日志详情")
    public Result<SysOperateLogVO> getById(@Parameter(description = "日志ID") @PathVariable Long id) {
        return Result.success(sysOperateLogService.getOperateLogById(id));
    }

}
