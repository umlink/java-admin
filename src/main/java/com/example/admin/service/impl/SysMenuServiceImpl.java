package com.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.common.constant.EntityConstants;
import com.example.admin.dto.MenuCreateDTO;
import com.example.admin.dto.MenuQueryDTO;
import com.example.admin.dto.MenuUpdateDTO;
import com.example.admin.entity.SysMenu;
import com.example.admin.exception.BusinessException;
import com.example.admin.mapper.SysMenuMapper;
import com.example.admin.service.SysMenuService;
import com.example.admin.service.SysRoleMenuService;
import com.example.admin.vo.MenuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysRoleMenuService sysRoleMenuService;

    @Override
    public List<MenuVO> getMenuTree() {
        // 查询所有启用的菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, EntityConstants.STATUS_ENABLED);
        wrapper.orderByAsc(SysMenu::getSortNum);
        List<SysMenu> allMenus = list(wrapper);

        // 构建树形结构
        return buildTree(allMenus, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuVO createMenu(MenuCreateDTO dto) {
        SysMenu menu = new SysMenu();
        menu.setParentId(dto.getParentId());
        menu.setMenuName(dto.getMenuName());
        menu.setMenuType(dto.getMenuType());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setPermissionCode(dto.getPermissionCode());
        menu.setSortNum(dto.getSortNum() != null ? dto.getSortNum() : 0);
        menu.setStatus(EntityConstants.STATUS_ENABLED);
        menu.setDeleted(EntityConstants.DELETED_NO);

        save(menu);
        return convertToVO(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuVO updateMenu(Long id, MenuUpdateDTO dto) {
        SysMenu menu = getById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }

        if (dto.getParentId() != null) {
            menu.setParentId(dto.getParentId());
        }
        if (dto.getMenuName() != null) {
            menu.setMenuName(dto.getMenuName());
        }
        if (dto.getMenuType() != null) {
            menu.setMenuType(dto.getMenuType());
        }
        if (dto.getPath() != null) {
            menu.setPath(dto.getPath());
        }
        if (dto.getComponent() != null) {
            menu.setComponent(dto.getComponent());
        }
        if (dto.getPermissionCode() != null) {
            menu.setPermissionCode(dto.getPermissionCode());
        }
        if (dto.getSortNum() != null) {
            menu.setSortNum(dto.getSortNum());
        }
        if (dto.getStatus() != null) {
            menu.setStatus(dto.getStatus());
        }

        updateById(menu);
        return convertToVO(menu);
    }

    @Override
    public MenuVO getMenuById(Long id) {
        SysMenu menu = getById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        return convertToVO(menu);
    }

    @Override
    public IPage<MenuVO> getMenuPage(MenuQueryDTO queryDTO) {
        int size = Math.min(queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10, 100);
        int pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;
        Page<SysMenu> page = new Page<>(pageNum, size);

        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getMenuName() != null && !queryDTO.getMenuName().isBlank()) {
            wrapper.like(SysMenu::getMenuName, queryDTO.getMenuName());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(SysMenu::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByAsc(SysMenu::getSortNum);

        return page(page, wrapper).convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        SysMenu menu = getById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        // 检查是否存在子菜单，有子菜单时禁止删除，防止产生悬空引用
        LambdaQueryWrapper<SysMenu> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(SysMenu::getParentId, id);
        if (count(childWrapper) > 0) {
            throw new BusinessException("存在子菜单，请先删除子菜单");
        }
        // 清除该菜单的角色关联，防止残留权限记录
        sysRoleMenuService.deleteByMenuId(id);
        removeById(id);
    }

    /**
     * 递归构建菜单树，返回 MenuVO 列表（含 children）
     */
    private List<MenuVO> buildTree(List<SysMenu> menus, Long parentId) {
        List<MenuVO> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                MenuVO vo = convertToVO(menu);
                vo.setChildren(buildTree(menus, menu.getId()));
                tree.add(vo);
            }
        }
        return tree;
    }

    /**
     * Entity 转换为 VO
     */
    private MenuVO convertToVO(SysMenu menu) {
        return MenuVO.builder()
                .id(menu.getId())
                .parentId(menu.getParentId())
                .menuName(menu.getMenuName())
                .menuType(menu.getMenuType())
                .path(menu.getPath())
                .component(menu.getComponent())
                .permissionCode(menu.getPermissionCode())
                .sortNum(menu.getSortNum())
                .status(menu.getStatus())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }
}
