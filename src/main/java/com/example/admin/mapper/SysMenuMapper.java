package com.example.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.admin.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据角色ID列表查询菜单列表
     */
    @Select("<script>" +
            "SELECT DISTINCT m.* FROM sys_role_menu rm " +
            "LEFT JOIN sys_menu m ON rm.menu_id = m.id " +
            "WHERE rm.role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "AND m.status = 1 AND m.deleted = 0 " +
            "ORDER BY m.sort_num ASC" +
            "</script>")
    List<SysMenu> selectMenusByRoleIds(@Param("roleIds") List<Long> roleIds);

}
