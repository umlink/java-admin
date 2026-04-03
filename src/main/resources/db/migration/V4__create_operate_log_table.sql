-- 创建操作日志表
CREATE TABLE IF NOT EXISTS `sys_operate_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `trace_id` VARCHAR(64) NULL COMMENT '链路追踪ID',
    `user_id` BIGINT NULL COMMENT '操作人ID',
    `username` VARCHAR(50) NULL COMMENT '操作人用户名',
    `module` VARCHAR(50) NULL COMMENT '操作模块',
    `description` VARCHAR(200) NULL COMMENT '操作描述',
    `request_method` VARCHAR(10) NULL COMMENT '请求方式',
    `request_uri` VARCHAR(255) NULL COMMENT '请求URI',
    `request_params` TEXT NULL COMMENT '请求参数（JSON）',
    `response_result` TEXT NULL COMMENT '响应结果（JSON）',
    `duration` BIGINT NULL COMMENT '执行时长（毫秒）',
    `ip_address` VARCHAR(50) NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) NULL COMMENT '用户代理',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '操作状态：0-失败，1-成功',
    `error_message` TEXT NULL COMMENT '错误信息',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_trace_id` (`trace_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
