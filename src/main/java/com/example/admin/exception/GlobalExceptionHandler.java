package com.example.admin.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.example.admin.common.api.Result;
import com.example.admin.common.api.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TRACE_ID_KEY = "traceId";

    /**
     * 处理未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLoginException(NotLoginException e, HttpServletRequest request) {
        String message = switch (e.getType()) {
            case NotLoginException.NOT_TOKEN -> "未提供登录凭证";
            case NotLoginException.INVALID_TOKEN -> "登录凭证无效";
            case NotLoginException.TOKEN_TIMEOUT -> "登录已过期，请重新登录";
            case NotLoginException.BE_REPLACED -> "登录已失效，账号已在其他设备登录";
            case NotLoginException.KICK_OUT -> "登录已失效，请重新登录";
            default -> "未登录或登录已失效";
        };
        log.warn("[{}] 未登录或登录态无效: type={}, message={}, uri={}",
                getTraceId(), e.getType(), e.getMessage(), request.getRequestURI());
        return Result.fail(ResultCode.UNAUTHORIZED, message);
    }

    /**
     * 处理无权限异常
     */
    @ExceptionHandler({NotPermissionException.class, NotRoleException.class})
    public Result<Void> handlePermissionException(Exception e, HttpServletRequest request) {
        log.warn("[{}] 无权限访问: message={}, uri={}",
                getTraceId(), e.getMessage(), request.getRequestURI());
        return Result.fail(ResultCode.FORBIDDEN, "无权限访问该资源");
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("[{}] 业务异常: message={}, uri={}",
                getTraceId(), e.getMessage(), request.getRequestURI());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理 @RequestBody 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        log.warn("[{}] 参数校验失败: message={}, uri={}",
                getTraceId(), message, request.getRequestURI());
        return Result.fail(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 处理 @RequestParam 参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("[{}] 参数校验失败: message={}, uri={}",
                getTraceId(), message, request.getRequestURI());
        return Result.fail(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 处理 @ModelAttribute 参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数绑定失败";
        log.warn("[{}] 参数绑定失败: message={}, uri={}",
                getTraceId(), message, request.getRequestURI());
        return Result.fail(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 处理请求参数缺失
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        String message = String.format("缺少必要参数: %s", e.getParameterName());
        log.warn("[{}] {}", getTraceId(), message);
        return Result.fail(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 处理请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String message = String.format("参数类型不匹配: %s", e.getName());
        log.warn("[{}] {}", getTraceId(), message);
        return Result.fail(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 处理请求体不可读
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("[{}] 请求体不可读: uri={}", getTraceId(), request.getRequestURI());
        return Result.fail(ResultCode.BAD_REQUEST, "请求体格式错误");
    }

    /**
     * 处理请求方法不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String message = String.format("不支持的请求方法: %s", e.getMethod());
        log.warn("[{}] {}", getTraceId(), message);
        return Result.fail(ResultCode.METHOD_NOT_ALLOWED, message);
    }

    /**
     * 处理媒体类型不支持
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result<Void> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        String message = "不支持的媒体类型";
        log.warn("[{}] {}", getTraceId(), message);
        return Result.fail(ResultCode.UNSUPPORTED_MEDIA_TYPE, message);
    }

    /**
     * 处理媒体类型不可接受
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public Result<Void> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e, HttpServletRequest request) {
        String message = "不可接受的媒体类型";
        log.warn("[{}] {}", getTraceId(), message);
        return Result.fail(ResultCode.NOT_ACCEPTABLE, message);
    }

    /**
     * 处理 404 未找到
     */
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public Result<Void> handleNotFoundException(Exception e, HttpServletRequest request) {
        log.warn("[{}] 请求资源不存在: uri={}", getTraceId(), request.getRequestURI());
        return Result.fail(ResultCode.NOT_FOUND, "请求的资源不存在");
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("[{}] 系统异常: uri={}", getTraceId(), request.getRequestURI(), e);
        return Result.fail(ResultCode.ERROR, "系统异常，请稍后重试");
    }

    /**
     * 获取 TraceId
     */
    private String getTraceId() {
        String traceId = MDC.get(TRACE_ID_KEY);
        return traceId != null ? traceId : "unknown";
    }

}
