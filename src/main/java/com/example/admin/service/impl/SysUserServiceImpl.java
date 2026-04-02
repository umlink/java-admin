package com.example.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.common.constant.EntityConstants;
import com.example.admin.config.StpInterfaceImpl;
import com.example.admin.dto.LoginDTO;
import com.example.admin.dto.UserAssignRoleDTO;
import com.example.admin.dto.UserCreateDTO;
import com.example.admin.dto.UserQueryDTO;
import com.example.admin.dto.UserUpdateDTO;
import com.example.admin.entity.SysRole;
import com.example.admin.entity.SysUser;
import com.example.admin.exception.BusinessException;
import com.example.admin.mapper.SysUserMapper;
import com.example.admin.service.SysRoleService;
import com.example.admin.service.SysUserRoleService;
import com.example.admin.service.SysUserService;
import com.example.admin.utils.LoginRateLimiter;
import com.example.admin.utils.PasswordUtil;
import com.example.admin.utils.RedisUtil;
import com.example.admin.vo.LoginVO;
import com.example.admin.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final LoginRateLimiter loginRateLimiter;

    private final RedisUtil redisUtil;

    @Override
    public LoginVO login(LoginDTO dto) {
        // 检查是否被锁定
        if (loginRateLimiter.isLocked(dto.getUsername())) {
            long remainingSeconds = loginRateLimiter.getLockRemainingSeconds(dto.getUsername());
            throw new BusinessException(String.format("账户已被锁定，请%d分钟后重试", (remainingSeconds + 59) / 60));
        }

        // 根据用户名查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        SysUser user = getOne(wrapper);

        if (user == null) {
            loginRateLimiter.recordFail(dto.getUsername());
            int remaining = loginRateLimiter.getRemainingAttempts(dto.getUsername());
            if (remaining > 0) {
                throw new BusinessException(String.format("用户名或密码错误，剩余尝试次数：%d", remaining));
            } else {
                throw new BusinessException("登录尝试次数过多，账户已被锁定30分钟");
            }
        }

        // 先校验状态，禁用账户不进行密码校验也不计失败次数
        if (user.getStatus() != EntityConstants.STATUS_ENABLED) {
            throw new BusinessException("用户已被禁用");
        }

        // 校验密码
        if (!PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
            loginRateLimiter.recordFail(dto.getUsername());
            int remaining = loginRateLimiter.getRemainingAttempts(dto.getUsername());
            if (remaining > 0) {
                throw new BusinessException(String.format("用户名或密码错误，剩余尝试次数：%d", remaining));
            } else {
                throw new BusinessException("登录尝试次数过多，账户已被锁定30分钟");
            }
        }

        // 登录成功，清除失败记录
        loginRateLimiter.recordSuccess(dto.getUsername());

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
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());

        // 加密密码
        user.setPassword(PasswordUtil.encode(dto.getPassword()));

        // 设置默认值
        user.setStatus(EntityConstants.STATUS_ENABLED);
        user.setDeleted(EntityConstants.DELETED_NO);

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
    public IPage<UserVO> getUserPage(UserQueryDTO queryDTO) {
        // 限制最大页大小，防止大查询拖垮数据库
        int size = Math.min(queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10, 100);
        int pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;

        Page<SysUser> page = new Page<>(pageNum, size);

        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 用户名模糊查询
        if (queryDTO.getUsername() != null && !queryDTO.getUsername().isBlank()) {
            wrapper.like(SysUser::getUsername, queryDTO.getUsername());
        }

        // 状态精确查询
        if (queryDTO.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, queryDTO.getStatus());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreatedAt);

        Page<SysUser> userPage = page(page, wrapper);
        return userPage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 禁止删除当前登录用户自身
        if (id.equals(StpUtil.getLoginIdAsLong())) {
            throw new BusinessException("不能删除当前登录用户");
        }
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 清除用户角色关联
        sysUserRoleService.deleteByUserId(id);
        removeById(id);
        // 清除权限缓存
        redisUtil.del(
                StpInterfaceImpl.PERM_KEY_PREFIX + id,
                StpInterfaceImpl.ROLE_KEY_PREFIX + id
        );
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
        // FOR UPDATE 锁住用户行，防止并发 assignRoles 互相覆盖
        SysUser user = getBaseMapper().selectForUpdate(dto.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 去重角色ID
        List<Long> roleIds = dto.getRoleIds().stream().distinct().collect(Collectors.toList());

        // 批量校验角色ID有效性（避免 N+1 查询）
        if (!roleIds.isEmpty()) {
            List<SysRole> roles = sysRoleService.listByIds(roleIds);
            if (roles.size() != roleIds.size()) {
                throw new BusinessException("存在无效的角色ID");
            }
        }

        // 先删除用户旧角色
        sysUserRoleService.deleteByUserId(dto.getUserId());

        // 再批量添加新角色
        sysUserRoleService.batchAdd(dto.getUserId(), roleIds);

        // 清除该用户的权限/角色缓存，使下次鉴权重新从DB加载
        redisUtil.del(
                StpInterfaceImpl.PERM_KEY_PREFIX + dto.getUserId(),
                StpInterfaceImpl.ROLE_KEY_PREFIX + dto.getUserId()
        );
    }
}