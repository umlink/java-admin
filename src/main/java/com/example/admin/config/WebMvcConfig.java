package com.example.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 为 Controller 接口统一添加 /api/v1 前缀
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 为 controller 包下的所有接口统一添加 /api/v1 前缀
        configurer.addPathPrefix("/api/v1",
            clazz -> clazz.getPackageName().startsWith("com.example.admin.controller"));
    }
}
