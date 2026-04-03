package com.example.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 标注在 Controller 方法上，自动记录操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperateLog {

    /**
     * 操作模块
     */
    String module() default "";

    /**
     * 操作描述
     */
    String description() default "";

    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;

    /**
     * 是否记录响应结果
     */
    boolean recordResult() default true;

    /**
     * 敏感字段列表，记录时会脱敏（替换为 ***）
     * 默认: password, token, secret, key
     */
    String[] sensitiveFields() default {"password", "token", "secret", "key"};

}
