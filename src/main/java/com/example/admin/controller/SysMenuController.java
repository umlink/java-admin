package com.example.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.admin.common.api.Result;
import com.example.admin.dto.MenuCreateDTO;
import com.example.admin.dto.MenuQueryDTO;
import com.example.admin.dto.MenuUpdateDTO;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.admin.common.constant.PermissionConstants;
import com.example.admin.service.SysMenuService;
import com.example.admin.vo.MenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 */
@RestController
@RequestMapping("/menu")
@Tag(name = "菜单管理", description = "菜单相关接口")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 获取菜单列表（分页，查询参数超过1个使用POST）
     */
    @PostMapping("/list")
    @SaCheckPermission(PermissionConstants.MENU_LIST)
    @Operation(summary = "获取菜���列表", description = "分页获取菜单列表，支持按菜单名称和状态筛选")
    public Result<IPage<MenuVO>> list(@RequestBody MenuQueryDTO queryDTO) {
        return Result.success(sysMenuService.getMenuPage(queryDTO));
    }

    /**
     * 获取菜单树
     */
    @GetMapping("/tree")
    @SaCheckPermission(PermissionConstants.MENU_LIST)
    @Operation(summary = "获取菜单树", description = "获取菜单树形结构")
    public Result<List<MenuVO>> getMenuTree() {
        return Result.success(sysMenuService.getMenuTree());
    }

    /**
     * 获取菜单详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission(PermissionConstants.MENU_LIST)
    @Operation(summary = "获取菜单详情", description = "根据ID获取菜单详情")
    public Result<MenuVO> getById(@Parameter(description = "菜单ID") @PathVariable Long id) {
        return Result.success(sysMenuService.getMenuById(id));
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @SaCheckPermission(PermissionConstants.MENU_CREATE)
    @Operation(summary = "新增菜单", description = "创建新菜单")
    public Result<MenuVO> save(@Valid @RequestBody MenuCreateDTO dto) {
        return Result.success(sysMenuService.createMenu(dto));
    }

    /**
     * 修改菜单
     */
    @PutMapping("/{id}")
    @SaCheckPermission(PermissionConstants.MENU_UPDATE)
    @Operation(summary = "修改菜单", description = "更新菜单信息")
    public Result<MenuVO> update(
            @Parameter(description = "菜单ID") @PathVariable Long id,
            @Valid @RequestBody MenuUpdateDTO dto) {
        return Result.success(sysMenuService.updateMenu(id, dto));
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission(PermissionConstants.MENU_DELETE)
    @Operation(summary = "删除菜单", description = "根据ID删除菜单")
    public Result<Void> delete(@Parameter(description = "菜单ID") @PathVariable Long id) {
        sysMenuService.deleteMenu(id);
        return Result.success();
    }
}