package com.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.dto.RoleAssignMenuDTO;
import com.example.admin.dto.RoleCreateDTO;
import com.example.admin.dto.RoleUpdateDTO;
import com.example.admin.entity.SysRole;
import com.example.admin.entity.SysMenu;
import com.example.admin.exception.BusinessException;
import com.example.admin.mapper.SysRoleMapper;
import com.example.admin.service.SysRoleMenuService;
import com.example.admin.service.SysRoleService;
import com.example.admin.service.SysMenuService;
import com.example.admin.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleVO createRole(RoleCreateDTO dto) {
        // 检查角色编码是否已存在
        if (existsByRoleCode(dto.getRoleCode(), null)) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);

        // 设置默认值
        role.setStatus(1);
        role.setDeleted(0);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

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

        role.setUpdatedAt(LocalDateTime.now());
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
    public IPage<RoleVO> getRolePage(Page<SysRole> page) {
        Page<SysRole> rolePage = page(page);
        return rolePage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        // 先删除角色菜单关联
        sysRoleMenuService.deleteByRoleId(id);
        // 再删除角色
        removeById(id);
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
        // 检查角色是否存在
        SysRole role = getById(dto.getRoleId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        List<Long> menuIds = dto.getMenuIds();
        if (menuIds != null && !menuIds.isEmpty()) {
            // 问题7：去重
            menuIds = menuIds.stream().distinct().collect(Collectors.toList());

            // 问题4：验证菜单ID是否存在
            for (Long menuId : menuIds) {
                SysMenu menu = sysMenuService.getById(menuId);
                if (menu == null) {
                    throw new BusinessException("菜单ID不存在: " + menuId);
                }
            }
        }

        // 先删除角色旧菜单
        sysRoleMenuService.deleteByRoleId(dto.getRoleId());

        // 再批量添加新菜单
        sysRoleMenuService.batchAdd(dto.getRoleId(), menuIds);
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