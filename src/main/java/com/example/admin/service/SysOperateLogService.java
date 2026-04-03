package com.example.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.admin.dto.SysOperateLogQueryDTO;
import com.example.admin.entity.SysOperateLog;
import com.example.admin.vo.SysOperateLogVO;

/**
 * 操作日志服务接口
 */
public interface SysOperateLogService extends IService<SysOperateLog> {

    /**
     * 分页查询操作日志
     *
     * @param queryDTO 查询条件
     * @return 分页操作日志列表
     */
    IPage<SysOperateLogVO> getOperateLogPage(SysOperateLogQueryDTO queryDTO);

    /**
     * 获取操作日志详情
     *
     * @param id 日志ID
     * @return 操作日志视图对象
     */
    SysOperateLogVO getOperateLogById(Long id);

    /**
     * 异步保存操作日志
     *
     * @param sysOperateLog 操作日志实体
     */
    void saveAsync(SysOperateLog sysOperateLog);

}
