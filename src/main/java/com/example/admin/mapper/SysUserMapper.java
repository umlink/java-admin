package com.example.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.admin.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询用户并加行锁（FOR UPDATE），用于需要串行化的写操作
     */
    @Select("SELECT * FROM sys_user WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    SysUser selectForUpdate(@Param("id") Long id);

}