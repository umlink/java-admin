package com.example.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.admin.dto.RoleAssignMenuDTO;
import com.example.admin.dto.RoleCreateDTO;
import com.example.admin.dto.RoleUpdateDTO;
import com.example.admin.entity.SysRole;
import com.example.admin.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 创建角色
     *
     * @param dto 角色创建请求
     * @return 角色视图对象
     */
    RoleVO createRole(RoleCreateDTO dto);

    /**
     * 更新角色
     *
     * @param id  角色ID
     * @param dto 角色更新请求
     * @return 角色视图对象
     */
    RoleVO updateRole(Long id, RoleUpdateDTO dto);

    /**
     * 获取角色详情
     *
     * @param id 角色ID
     * @return 角色视图对象
     */
    RoleVO getRoleById(Long id);

    /**
     * 分页获取角色列表
     *
     * @param page 分页参数
     * @return 分页角色列表
     */
    IPage<RoleVO> getRolePage(Page<SysRole> page);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 检查角色编码是否已存在
     *
     * @param roleCode  角色编码
     * @param excludeId 排除的角色ID（可选）
     * @return 是否存在
     */
    boolean existsByRoleCode(String roleCode, Long excludeId);

    /**
     * 获取角色拥有的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 给角色分配菜单
     *
     * @param dto 分配请求
     */
    void assignMenus(RoleAssignMenuDTO dto);
}