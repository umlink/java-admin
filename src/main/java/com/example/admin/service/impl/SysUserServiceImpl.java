package com.example.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.dto.LoginDTO;
import com.example.admin.dto.UserAssignRoleDTO;
import com.example.admin.dto.UserCreateDTO;
import com.example.admin.dto.UserUpdateDTO;
import com.example.admin.entity.SysRole;
import com.example.admin.entity.SysUser;
import com.example.admin.exception.BusinessException;
import com.example.admin.mapper.SysUserMapper;
import com.example.admin.service.SysRoleService;
import com.example.admin.service.SysUserRoleService;
import com.example.admin.service.SysUserService;
import com.example.admin.utils.PasswordUtil;
import com.example.admin.vo.LoginVO;
import com.example.admin.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleService sysUserRoleService;

    private final SysRoleService sysRoleService;

    @Override
    public LoginVO login(LoginDTO dto) {
        // 根据用户名查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        SysUser user = getOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 校验密码
        if (!PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 校验用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }

        // Sa-Token 登录
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        // 返回登录信息
        return LoginVO.builder()
                .token(token)
                .user(convertToVO(user))
                .build();
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO createUser(UserCreateDTO dto) {
        // 检查用户名是否已存在
        if (existsByUsername(dto.getUsername(), null)) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);

        // 加密密码
        user.setPassword(PasswordUtil.encode(dto.getPassword()));

        // 设置默认值
        user.setStatus(1);
        user.setDeleted(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        save(user);

        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateUser(Long id, UserUpdateDTO dto) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }

        user.setUpdatedAt(LocalDateTime.now());
        updateById(user);

        return convertToVO(user);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    public IPage<UserVO> getUserPage(Page<SysUser> page) {
        Page<SysUser> userPage = page(page);
        return userPage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        removeById(id);
    }

    @Override
    public boolean existsByUsername(String username, Long excludeId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    /**
     * Entity 转换为 VO
     */
    private UserVO convertToVO(SysUser user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return sysUserRoleService.getRoleIdsByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(UserAssignRoleDTO dto) {
        // 检查用户是否存在
        SysUser user = getById(dto.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 去重角色ID
        List<Long> roleIds = dto.getRoleIds().stream().distinct().collect(Collectors.toList());

        // 验证角色ID有效性
        for (Long roleId : roleIds) {
            SysRole role = sysRoleService.getById(roleId);
            if (role == null) {
                throw new BusinessException("角色ID不存在: " + roleId);
            }
        }

        // 先删除用户旧角色
        sysUserRoleService.deleteByUserId(dto.getUserId());

        // 再批量添加新角色
        sysUserRoleService.batchAdd(dto.getUserId(), roleIds);
    }
}