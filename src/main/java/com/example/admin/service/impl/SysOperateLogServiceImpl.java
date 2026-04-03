package com.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.dto.SysOperateLogQueryDTO;
import com.example.admin.entity.SysOperateLog;
import com.example.admin.exception.BusinessException;
import com.example.admin.mapper.SysOperateLogMapper;
import com.example.admin.service.SysOperateLogService;
import com.example.admin.vo.SysOperateLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 操作日志服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOperateLogServiceImpl extends ServiceImpl<SysOperateLogMapper, SysOperateLog> implements SysOperateLogService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public IPage<SysOperateLogVO> getOperateLogPage(SysOperateLogQueryDTO queryDTO) {
        Page<SysOperateLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SysOperateLog> wrapper = new LambdaQueryWrapper<>();

        // 用户名模糊查询
        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like(SysOperateLog::getUsername, queryDTO.getUsername());
        }

        // 模块精确查询
        if (StringUtils.hasText(queryDTO.getModule())) {
            wrapper.eq(SysOperateLog::getModule, queryDTO.getModule());
        }

        // 状态精确查询
        if (queryDTO.getStatus() != null) {
            wrapper.eq(SysOperateLog::getStatus, queryDTO.getStatus());
        }

        // 时间范围查询
        if (StringUtils.hasText(queryDTO.getStartTime())) {
            try {
                wrapper.ge(SysOperateLog::getCreatedAt, LocalDateTime.parse(queryDTO.getStartTime(), DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                log.warn("开始时间格式错误: {}", queryDTO.getStartTime());
                throw new BusinessException("开始时间格式错误，正确格式：yyyy-MM-dd HH:mm:ss");
            }
        }
        if (StringUtils.hasText(queryDTO.getEndTime())) {
            try {
                wrapper.le(SysOperateLog::getCreatedAt, LocalDateTime.parse(queryDTO.getEndTime(), DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                log.warn("结束时间格式错误: {}", queryDTO.getEndTime());
                throw new BusinessException("结束时间格式错误，正确格式：yyyy-MM-dd HH:mm:ss");
            }
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysOperateLog::getCreatedAt);

        IPage<SysOperateLog> entityPage = page(page, wrapper);

        // 转换为 VO
        Page<SysOperateLogVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    public SysOperateLogVO getOperateLogById(Long id) {
        SysOperateLog entity = getById(id);
        if (entity == null) {
            return null;
        }
        return convertToVO(entity);
    }

    /**
     * Entity 转 VO
     */
    private SysOperateLogVO convertToVO(SysOperateLog entity) {
        SysOperateLogVO vo = new SysOperateLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 异步保存操作日志
     */
    @Override
    @Async
    public void saveAsync(SysOperateLog sysOperateLog) {
        try {
            save(sysOperateLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
    }

}
