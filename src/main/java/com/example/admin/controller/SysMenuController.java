package com.example.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.admin.common.api.Result;
import com.example.admin.dto.MenuCreateDTO;
import com.example.admin.dto.MenuUpdateDTO;
import com.example.admin.entity.SysMenu;
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
@RequestMapping("/api/menu")
@Tag(name = "菜单管理", description = "菜单相关接口")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 获取菜单列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取菜单列表", description = "分页获取菜单列表")
    public Result<IPage<MenuVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(sysMenuService.getMenuPage(new Page<>(page, size)));
    }

    /**
     * 获取菜单树
     */
    @GetMapping("/tree")
    @Operation(summary = "获取菜单树", description = "获取菜单树形结构")
    public Result<List<SysMenu>> getMenuTree() {
        return Result.success(sysMenuService.getMenuTree());
    }

    /**
     * 获取菜单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取菜单详情", description = "根据ID获取菜单详情")
    public Result<MenuVO> getById(@Parameter(description = "菜单ID") @PathVariable Long id) {
        return Result.success(sysMenuService.getMenuById(id));
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @Operation(summary = "新增菜单", description = "创建新菜单")
    public Result<MenuVO> save(@Valid @RequestBody MenuCreateDTO dto) {
        return Result.success(sysMenuService.createMenu(dto));
    }

    /**
     * 修改菜单
     */
    @PutMapping("/{id}")
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
    @Operation(summary = "删除菜单", description = "根据ID删除菜单")
    public Result<Void> delete(@Parameter(description = "菜单ID") @PathVariable Long id) {
        sysMenuService.deleteMenu(id);
        return Result.success();
    }
}