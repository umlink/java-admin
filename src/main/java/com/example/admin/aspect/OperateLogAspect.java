package com.example.admin.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.example.admin.annotation.OperateLog;
import com.example.admin.entity.SysOperateLog;
import com.example.admin.service.SysOperateLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 操作日志切面
 * 使用 @Around 环绕通知记录请求参数、响应结果、执行时长
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperateLogAspect {

    private static final String TRACE_ID_KEY = "traceId";
    private static final int MAX_LENGTH = 4000;

    private final ObjectMapper objectMapper;
    private final SysOperateLogService sysOperateLogService;

    @Around("@annotation(com.example.admin.annotation.OperateLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable throwable = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            try {
                saveOperateLog(joinPoint, result, throwable, duration);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
    }

    /**
     * 保存操作日志
     */
    private void saveOperateLog(ProceedingJoinPoint joinPoint, Object result, Throwable throwable, long duration) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperateLog operateLog = method.getAnnotation(OperateLog.class);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        SysOperateLog sysOperateLog = new SysOperateLog();

        // 链路追踪ID
        sysOperateLog.setTraceId(MDC.get(TRACE_ID_KEY));

        // 操作人信息
        if (StpUtil.isLogin()) {
            sysOperateLog.setUserId(StpUtil.getLoginIdAsLong());
            sysOperateLog.setUsername((String) StpUtil.getSession().get("username"));
        }

        // 操作模块与描述
        sysOperateLog.setModule(operateLog.module());
        sysOperateLog.setDescription(operateLog.description());

        // 请求信息
        sysOperateLog.setRequestMethod(request.getMethod());
        sysOperateLog.setRequestUri(request.getRequestURI());
        sysOperateLog.setIpAddress(getIpAddress(request));
        sysOperateLog.setUserAgent(request.getHeader("User-Agent"));

        // 请求参数
        if (operateLog.recordParams()) {
            sysOperateLog.setRequestParams(getRequestParams(request, joinPoint));
        }

        // 响应结果
        if (operateLog.recordResult() && result != null) {
            sysOperateLog.setResponseResult(truncate(toJson(result)));
        }

        // 执行时长
        sysOperateLog.setDuration(duration);

        // 状态与错误信息
        if (throwable != null) {
            sysOperateLog.setStatus(0);
            sysOperateLog.setErrorMessage(truncate(throwable.getMessage()));
        } else {
            sysOperateLog.setStatus(1);
        }

        sysOperateLog.setCreatedAt(LocalDateTime.now());

        sysOperateLogService.saveAsync(sysOperateLog);
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();

        // GET 参数
        String queryString = request.getQueryString();
        if (queryString != null) {
            sb.append(queryString);
        }

        // POST JSON 参数
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                if (!sb.isEmpty()) {
                    sb.append("&");
                }
                sb.append(new String(content, StandardCharsets.UTF_8));
            }
        }

        // 如果上述方式没获取到，尝试从方法参数获取
        if (sb.isEmpty()) {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                String params = Arrays.stream(args)
                        .filter(arg -> !(arg instanceof HttpServletRequest))
                        .map(this::toJson)
                        .collect(Collectors.joining(","));
                sb.append(params);
            }
        }

        return truncate(sb.toString());
    }

    /**
     * 获取 IP 地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                int index = ip.indexOf(',');
                if (index != -1) {
                    return ip.substring(0, index).trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * 对象转 JSON
     */
    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }

    /**
     * 截断超长字符串
     */
    private String truncate(String str) {
        if (str == null || str.length() <= MAX_LENGTH) {
            return str;
        }
        return str.substring(0, MAX_LENGTH);
    }

}
