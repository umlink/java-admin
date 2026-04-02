package com.example.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.admin.dto.LoginDTO;
import com.example.admin.dto.UserAssignRoleDTO;
import com.example.admin.dto.UserCreateDTO;
import com.example.admin.dto.UserQueryDTO;
import com.example.admin.dto.UserUpdateDTO;
import com.example.admin.entity.SysUser;
import com.example.admin.vo.LoginVO;
import com.example.admin.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 登录
     *
     * @param dto 登录请求
     * @return 登录响应
     */
    LoginVO login(LoginDTO dto);

    /**
     * 登出
     */
    void logout();

    /**
     * 获取当前登录用户
     *
     * @return 用户视图对象
     */
    UserVO getCurrentUser();

    /**
     * 创建用户
     *
     * @param dto 用户创建请求
     * @return 用户视图对象
     */
    UserVO createUser(UserCreateDTO dto);

    /**
     * 更新用户
     *
     * @param id  用户ID
     * @param dto 用户更新请求
     * @return 用户视图对象
     */
    UserVO updateUser(Long id, UserUpdateDTO dto);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户视图对象
     */
    UserVO getUserById(Long id);

    /**
     * 分页获取用户列表（支持条件筛选）
     *
     * @param queryDTO 查询条件
     * @return 分页用户列表
     */
    IPage<UserVO> getUserPage(UserQueryDTO queryDTO);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 检查用户名是否已存在
     *
     * @param username 用户名
     * @param excludeId 排除的用户ID（可选）
     * @return 是否存在
     */
    boolean existsByUsername(String username, Long excludeId);

    /**
     * 获取用户拥有的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 给用户分配角色
     *
     * @param dto 分配请求
     */
    void assignRoles(UserAssignRoleDTO dto);
}