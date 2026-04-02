package com.example.admin.config;

import cn.dev33.satoken.stp.StpInterface;
import com.example.admin.common.constant.PermissionConstants;
import com.example.admin.mapper.SysRoleMenuMapper;
import com.example.admin.mapper.SysUserRoleMapper;
import com.example.admin.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限数据源实现。
 * 查询结果缓存到 Redis，TTL 5 分钟，避免每次鉴权都打数据库。
 * 缓存失效时机：用户角色变更（assignRoles）、角色菜单变更（assignMenus）时主动删除。
 *
 * 超管角色（ADMIN）直接返回通配符 "*"，跳过所有权限校验，无需维护数据库权限记录。
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    /** 超管角色编码，拥有全部权限 */
    private static final String SUPER_ADMIN_ROLE = PermissionConstants.SUPER_ADMIN_ROLE;

    /** 权限码缓存 key 前缀，格式：perm:user:{userId} */
    public static final String PERM_KEY_PREFIX = "perm:user:";

    /** 角色码缓存 key 前缀，格式：role:user:{userId} */
    public static final String ROLE_KEY_PREFIX = "role:user:";

    /** 缓存 TTL（秒），5 分钟 */
    private static final long CACHE_TTL = 300L;

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final RedisUtil redisUtil;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 先取角色列表（走缓存），超管直接返回通配符，跳过所有 @SaCheckPermission 校验
        List<String> roleCodes = getCachedRoleList(loginId);
        if (roleCodes.contains(SUPER_ADMIN_ROLE)) {
            return List.of("*");
        }

        final String key = PERM_KEY_PREFIX + loginId;
        List<String> cached = getListFromCache(key);
        if (cached != null) {
            return cached;
        }

        final Long userId = Long.valueOf(loginId.toString());
        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> permissions = sysRoleMenuMapper.selectPermissionCodesByRoleIds(roleIds);
        final List<String> result = permissions != null ? permissions : new ArrayList<>();
        redisUtil.set(key, result, CACHE_TTL);
        return result;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return getCachedRoleList(loginId);
    }

    /**
     * 获取角色码列表（带缓存），供 getPermissionList 和 getRoleList 共用，避免重复查询。
     */
    @SuppressWarnings("unchecked")
    private List<String> getCachedRoleList(Object loginId) {
        final String key = ROLE_KEY_PREFIX + loginId;
        List<String> cached = getListFromCache(key);
        if (cached != null) {
            return cached;
        }

        final Long userId = Long.valueOf(loginId.toString());
        List<String> roleCodes = sysUserRoleMapper.selectRoleCodesByUserId(userId);
        final List<String> result = roleCodes != null ? roleCodes : new ArrayList<>();
        redisUtil.set(key, result, CACHE_TTL);
        return result;
    }

    /**
     * 从 Redis 取 List<String>，类型不匹配时返回 null。
     */
    @SuppressWarnings("unchecked")
    private List<String> getListFromCache(String key) {
        Object cached = redisUtil.get(key);
        if (cached instanceof List<?>) {
            return (List<String>) cached;
        }
        return null;
    }
}
