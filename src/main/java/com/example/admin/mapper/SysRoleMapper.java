package com.example.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.admin.entity.SysRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 角色Mapper
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 查询角色并加行锁（FOR UPDATE），用于需要串行化的写操作
     */
    @Select("SELECT * FROM sys_role WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    SysRole selectForUpdate(@Param("id") Long id);

}