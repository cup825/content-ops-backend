package com.bytedance.content.common.exception;

import com.bytedance.content.common.vo.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 * 使用 @RestControllerAdvice 拦截所有 @RestController 抛出的异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理业务自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Object> handleBusinessException(BusinessException e) {
        logger.warn("Business exception occurred: {}", e.getMessage());
        return ApiResponse.fail(e.getCode(), e.getMessage());
    }
    
    /**
     * 处理 404 (NotFound) 异常 - 接口路径不存在
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResponse<Object> handleNotFoundException(NoHandlerFoundException e) {
        logger.warn("404 Not Found: {}", e.getRequestURL());
        return ApiResponse.fail(404, "接口路径不存在，请检查 URL");
    }
    
    /**
     * 处理运行时异常 - 兜底处理
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<Object> handleRuntimeException(RuntimeException e) {
        logger.error("Runtime exception occurred: ", e);
        return ApiResponse.fail(500, "服务器开小差了，请稍后重试");
    }
    
    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception e) {
        logger.error("Unexpected exception occurred: ", e);
        return ApiResponse.fail(500, "系统内部错误，请联系管理员");
    }
}

