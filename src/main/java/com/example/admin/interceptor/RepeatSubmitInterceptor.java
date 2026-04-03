package com.example.admin.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.example.admin.annotation.RepeatSubmit;
import com.example.admin.exception.BusinessException;
import com.example.admin.utils.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 防重复提交拦截器
 * 使用 Redis 实现：Key = repeat:{userId}:{uri}:{md5(params)}，TTL = 注解配置的间隔时间
 * 使用 SETNX 原子操作避免竞态条件
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RepeatSubmitInterceptor implements HandlerInterceptor {

    private static final String REPEAT_KEY_PREFIX = "repeat:";
    private static final String ANONYMOUS_USER = "anonymous";
    private static final String REPEAT_MARKER = "1";

    private final RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Method method = handlerMethod.getMethod();
        RepeatSubmit repeatSubmit = method.getAnnotation(RepeatSubmit.class);
        if (repeatSubmit == null) {
            return true;
        }

        // 构建防重 Key
        String key = buildRepeatKey(request);
        log.debug("防重复提交检查，key: {}", key);

        // 原子操作：仅当 key 不存在时设置，避免竞态条件
        boolean success = redisUtil.setIfAbsent(key, REPEAT_MARKER, repeatSubmit.interval());
        if (!success) {
            log.warn("重复提交拦截，key: {}", key);
            throw new BusinessException("请勿重复提交");
        }

        return true;
    }

    /**
     * 构建防重 Key：repeat:{userId}:{uri}:{md5(params)}
     */
    private String buildRepeatKey(HttpServletRequest request) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(REPEAT_KEY_PREFIX);

        // 用户ID：未登录时使用 anonymous
        if (StpUtil.isLogin()) {
            sb.append(StpUtil.getLoginIdAsString());
        } else {
            sb.append(ANONYMOUS_USER);
        }
        sb.append(":");

        // 请求 URI
        sb.append(request.getRequestURI());
        sb.append(":");

        // 请求参数 MD5
        String params = getRequestParams(request);
        sb.append(md5(params));

        return sb.toString();
    }

    /**
     * 获取请求参数（GET + POST）
     */
    private String getRequestParams(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        // GET 参数
        String queryString = request.getQueryString();
        if (queryString != null) {
            sb.append(queryString);
        }

        // POST 参数（JSON）
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                if (!sb.isEmpty()) {
                    sb.append("&");
                }
                sb.append(new String(content, StandardCharsets.UTF_8));
            }
        }

        return sb.toString();
    }

    /**
     * MD5 加密
     */
    private String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
