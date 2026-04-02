package com.example.admin.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 拦截器配置：JWT 模式 + 注解鉴权。
 * 权限数据源实现见 {@link StpInterfaceImpl}。
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    private static final String[] OPEN_ENDPOINTS = {
            "/health/**",
            "/auth/login",
            "/doc.html",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/webjars/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/favicon.ico",
            "/error",
            "/error/**"
    };

    /**
     * Sa-Token 整合 JWT
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 开启注解鉴权（@SaCheckLogin、@SaCheckPermission 等），同时对非公开路径强制校验登录
        registry.addInterceptor(new SaInterceptor(handle ->
                SaRouter.match("/**")
                        .notMatch(OPEN_ENDPOINTS)
                        .check(r -> StpUtil.checkLogin())
        ).isAnnotation(true))
                .addPathPatterns("/**")
                .excludePathPatterns(OPEN_ENDPOINTS);
    }
}
