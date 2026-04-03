package com.example.admin.config;

import com.example.admin.interceptor.RepeatSubmitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 为 Controller 接口统一添加 /api/v1 前缀
 * 配置防重复提交拦截器
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RepeatSubmitInterceptor repeatSubmitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 配置防重复提交拦截器
        registry.addInterceptor(repeatSubmitInterceptor)
                .addPathPatterns("/**")
                .order(1);
    }

}
