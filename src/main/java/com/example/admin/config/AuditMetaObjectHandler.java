package com.example.admin.config;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * MyBatis-Plus 审计字段自动填充处理器。
 * 仅负责填充 createdBy / updatedBy（操作人 ID），
 * createdAt / updatedAt 由数据库 DEFAULT CURRENT_TIMESTAMP / ON UPDATE CURRENT_TIMESTAMP 接管。
 * 禁止在 Service 层手动赋值这两个操作人字段。
 */
@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        fillOperator(metaObject, "createdBy");
        fillOperator(metaObject, "updatedBy");
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        fillOperator(metaObject, "updatedBy");
    }

    /**
     * 安全填充操作人字段：字段不存在或已有值时跳过；未登录（如初始化脚本）时跳过，不抛异常。
     */
    private void fillOperator(MetaObject metaObject, String fieldName) {
        if (!metaObject.hasSetter(fieldName)) {
            return;
        }
        if (metaObject.getValue(fieldName) != null) {
            return;
        }
        if (StpUtil.isLogin()) {
            final Long userId = Long.valueOf(StpUtil.getLoginIdAsString());
            this.setFieldValByName(fieldName, userId, metaObject);
        }
    }
}
