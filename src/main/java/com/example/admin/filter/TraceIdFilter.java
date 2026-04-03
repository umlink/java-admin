package com.example.admin.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 链路追踪 Filter
 * 在请求入口生成 TraceId，放入 MDC 中，日志输出时自动包含
 */
@Slf4j
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 从请求头获取 TraceId，如果没有则生成新的
            String traceId = request.getHeader(TRACE_ID_HEADER);
            if (traceId == null || traceId.trim().isEmpty()) {
                traceId = generateTraceId();
            }

            // 放入 MDC
            MDC.put(TRACE_ID_KEY, traceId);

            // 在响应头中返回 TraceId
            response.setHeader(TRACE_ID_HEADER, traceId);

            log.debug("请求开始，TraceId: {}, URI: {}", traceId, request.getRequestURI());

            filterChain.doFilter(request, response);

        } finally {
            // 清理 MDC，防止线程复用时污染
            log.debug("请求结束，TraceId: {}", MDC.get(TRACE_ID_KEY));
            MDC.remove(TRACE_ID_KEY);
        }
    }

    /**
     * 生成 TraceId
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
