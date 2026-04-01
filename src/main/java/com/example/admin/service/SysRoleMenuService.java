package com.example.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.admin.entity.SysRoleMenu;

import java.util.List;

/**
 * 角色菜单关联服务接口
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * 获取角色拥有的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 批量删除角色菜单关联
     *
     * @param roleId 角色ID
     */
    void deleteByRoleId(Long roleId);

    /**
     * 批量添加角色菜单关联
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     */
    void batchAdd(Long roleId, List<Long> menuIds);
}