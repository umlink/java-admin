package com.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.entity.SysUserRole;
import com.example.admin.mapper.SysUserRoleMapper;
import com.example.admin.service.SysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户角色关联服务实现类
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        wrapper.select(SysUserRole::getRoleId);
        return list(wrapper).stream().map(SysUserRole::getRoleId).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        remove(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAdd(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        List<SysUserRole> list = new ArrayList<>();
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreatedAt(LocalDateTime.now());
            list.add(userRole);
        }
        saveBatch(list);
    }
}