package com.example.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.admin.entity.SysUserRole;

import java.util.List;

/**
 * 用户角色关联服务接口
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 获取用户拥有的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * 批量删除用户角色关联
     *
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);

    /**
     * 批量添加用户角色关联
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void batchAdd(Long userId, List<Long> roleIds);
}