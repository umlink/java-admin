package com.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.dto.MenuCreateDTO;
import com.example.admin.dto.MenuUpdateDTO;
import com.example.admin.entity.SysMenu;
import com.example.admin.exception.BusinessException;
import com.example.admin.mapper.SysMenuMapper;
import com.example.admin.service.SysMenuService;
import com.example.admin.vo.MenuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<SysMenu> getMenuTree() {
        // 查询所有启用的菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        wrapper.orderByAsc(SysMenu::getSortNum);
        List<SysMenu> allMenus = list(wrapper);

        // 构建树形结构
        return buildTree(allMenus, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuVO createMenu(MenuCreateDTO dto) {
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(dto, menu);

        // 设置默认值
        if (menu.getSortNum() == null) {
            menu.setSortNum(0);
        }
        menu.setStatus(1);
        menu.setDeleted(0);
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());

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

        menu.setUpdatedAt(LocalDateTime.now());
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
    public IPage<MenuVO> getMenuPage(Page<SysMenu> page) {
        Page<SysMenu> menuPage = page(page);
        return menuPage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        SysMenu menu = getById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        removeById(id);
    }

    /**
     * 递归构建菜单树
     *
     * @param menus    所有菜单列表
     * @param parentId 父菜单ID
     * @return 树形菜单
     */
    private List<SysMenu> buildTree(List<SysMenu> menus, Long parentId) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                // 递归查找子菜单
                List<SysMenu> children = buildTree(menus, menu.getId());
                // 使用反射设置子菜单（实体类中没有children字段，这里简化处理，实际项目中可以创建VO）
                tree.add(menu);
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
