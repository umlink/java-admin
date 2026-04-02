package com.example.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.admin.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色菜单关联Mapper
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据角色ID列表查询权限编码列表
     */
    @Select("<script>" +
            "SELECT DISTINCT m.permission_code FROM sys_role_menu rm " +
            "LEFT JOIN sys_menu m ON rm.menu_id = m.id " +
            "WHERE rm.role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "AND m.permission_code IS NOT NULL AND m.permission_code != '' " +
            "AND m.status = 1 AND m.deleted = 0" +
            "</script>")
    List<String> selectPermissionCodesByRoleIds(@Param("roleIds") List<Long> roleIds);
}