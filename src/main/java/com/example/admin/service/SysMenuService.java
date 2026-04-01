package com.example.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.admin.dto.MenuCreateDTO;
import com.example.admin.dto.MenuUpdateDTO;
import com.example.admin.entity.SysMenu;
import com.example.admin.vo.MenuVO;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取菜单树形结构
     *
     * @return 菜单树
     */
    List<SysMenu> getMenuTree();

    /**
     * 创建菜单
     *
     * @param dto 菜单创建请求
     * @return 菜单视图对象
     */
    MenuVO createMenu(MenuCreateDTO dto);

    /**
     * 更新菜单
     *
     * @param id  菜单ID
     * @param dto 菜单更新请求
     * @return 菜单视图对象
     */
    MenuVO updateMenu(Long id, MenuUpdateDTO dto);

    /**
     * 根据ID获取菜单详情
     *
     * @param id 菜单ID
     * @return 菜单视图对象
     */
    MenuVO getMenuById(Long id);

    /**
     * 分页查询菜单
     *
     * @param page 分页参数
     * @return 菜单分页数据
     */
    IPage<MenuVO> getMenuPage(Page<SysMenu> page);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    void deleteMenu(Long id);
}