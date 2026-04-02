package com.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.common.constant.EntityConstants;
import com.example.admin.config.StpInterfaceImpl;
import com.example.admin.dto.RoleAssignMenuDTO;
import com.example.admin.dto.RoleCreateDTO;
import com.example.admin.dto.RoleQueryDTO;
import com.example.admin.dto.RoleUpdateDTO;
import com.example.admin.entity.SysRole;
import com.example.admin.entity.SysMenu;
import com.example.admin.exception.BusinessException;
import com.example.admin.mapper.SysRoleMapper;
import com.example.admin.service.SysRoleMenuService;
import com.example.admin.service.SysRoleService;
import com.example.admin.service.SysMenuService;
import com.example.admin.service.SysUserRoleService;
import com.example.admin.utils.RedisUtil;
import com.example.admin.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuService sysRoleMenuService;

    private final SysMenuService sysMenuService;

    private final SysUserRoleService sysUserRoleService;

    private final RedisUtil redisUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleVO createRole(RoleCreateDTO dto) {
        // 检查角色编码是否已存在
        if (existsByRoleCode(dto.getRoleCode(), null)) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole role = new SysRole();
        role.setRoleCode(dto.getRoleCode());
        role.setRoleName(dto.getRoleName());
        role.setRemark(dto.getRemark());

        // 设置默认值
        role.setStatus(EntityConstants.STATUS_ENABLED);
        role.setDeleted(EntityConstants.DELETED_NO);

        save(role);

        return convertToVO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleVO updateRole(Long id, RoleUpdateDTO dto) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 如果更新了角色编码，检查是否重复
        if (dto.getRoleCode() != null && !dto.getRoleCode().equals(role.getRoleCode())) {
            if (existsByRoleCode(dto.getRoleCode(), id)) {
                throw new BusinessException("角色编码已存在");
            }
            role.setRoleCode(dto.getRoleCode());
        }

        if (dto.getRoleName() != null) {
            role.setRoleName(dto.getRoleName());
        }
        if (dto.getStatus() != null) {
            role.setStatus(dto.getStatus());
        }
        if (dto.getRemark() != null) {
            role.setRemark(dto.getRemark());
        }

        updateById(role);

        return convertToVO(role);
    }

    @Override
    public RoleVO getRoleById(Long id) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return convertToVO(role);
    }

    @Override
    public IPage<RoleVO> getRolePage(RoleQueryDTO queryDTO) {
        int size = Math.min(queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10, 100);
        int pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;
        Page<SysRole> page = new Page<>(pageNum, size);

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getRoleName() != null && !queryDTO.getRoleName().isBlank()) {
            wrapper.like(SysRole::getRoleName, queryDTO.getRoleName());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByDesc(SysRole::getCreatedAt);

        return page(page, wrapper).convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        // 查出该角色下所有用户ID，用于后续清缓存
        List<Long> affectedUserIds = sysUserRoleService.getUserIdsByRoleId(id);
        // 清除用户角色关联
        sysUserRoleService.deleteByRoleId(id);
        // 清除角色菜单关联
        sysRoleMenuService.deleteByRoleId(id);
        // 删除角色
        removeById(id);
        // 清除受影响用户的权限缓存
        if (!affectedUserIds.isEmpty()) {
            final String[] keys = affectedUserIds.stream()
                    .flatMap(uid -> java.util.stream.Stream.of(
                            StpInterfaceImpl.PERM_KEY_PREFIX + uid,
                            StpInterfaceImpl.ROLE_KEY_PREFIX + uid
                    ))
                    .toArray(String[]::new);
            redisUtil.del(keys);
        }
    }

    @Override
    public boolean existsByRoleCode(String roleCode, Long excludeId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode);
        if (excludeId != null) {
            wrapper.ne(SysRole::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return sysRoleMenuService.getMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(RoleAssignMenuDTO dto) {
        // FOR UPDATE 锁住角色行，防止并发 assignMenus 互相覆盖
        SysRole role = getBaseMapper().selectForUpdate(dto.getRoleId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        List<Long> menuIds = dto.getMenuIds() != null
                ? dto.getMenuIds().stream().distinct().collect(Collectors.toList())
                : new ArrayList<>();

        if (!menuIds.isEmpty()) {
            // 批量校验菜单ID有效性（避免 N+1 查询）
            List<SysMenu> menus = sysMenuService.listByIds(menuIds);
            if (menus.size() != menuIds.size()) {
                throw new BusinessException("存在无效的菜单ID");
            }
        }

        // 先删除角色旧菜单
        sysRoleMenuService.deleteByRoleId(dto.getRoleId());

        // 再批量添加新菜单
        sysRoleMenuService.batchAdd(dto.getRoleId(), menuIds);

        // 清除该角色下所有用户的权限缓存，使下次鉴权重新从DB加载
        List<Long> userIds = sysUserRoleService.getUserIdsByRoleId(dto.getRoleId());
        if (!userIds.isEmpty()) {
            final String[] keys = userIds.stream()
                    .flatMap(uid -> java.util.stream.Stream.of(
                            StpInterfaceImpl.PERM_KEY_PREFIX + uid,
                            StpInterfaceImpl.ROLE_KEY_PREFIX + uid
                    ))
                    .toArray(String[]::new);
            redisUtil.del(keys);
        }
    }

    /**
     * Entity 转换为 VO
     */
    private RoleVO convertToVO(SysRole role) {
        return RoleVO.builder()
                .id(role.getId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .status(role.getStatus())
                .remark(role.getRemark())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}