package com.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.entity.SysRoleMenu;
import com.example.admin.mapper.SysRoleMenuMapper;
import com.example.admin.service.SysRoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色菜单关联服务实现类
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        wrapper.select(SysRoleMenu::getMenuId);
        return list(wrapper).stream().map(SysRoleMenu::getMenuId).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        remove(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(Long roleId, List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }

        List<SysRoleMenu> list = new ArrayList<>();
        for (Long menuId : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenu.setCreatedAt(LocalDateTime.now());
            list.add(roleMenu);
        }
        saveBatch(list);
    }
}