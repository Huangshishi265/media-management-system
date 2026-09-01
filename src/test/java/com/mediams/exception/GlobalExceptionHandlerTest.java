package com.mediams.exception;

import com.mediams.dto.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 全局异常处理器测试
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    @DisplayName("处理业务异常 - 成功")
    void testHandleBusinessException() {
        BusinessException exception = new BusinessException("业务异常测试");

        Result<Void> result = exceptionHandler.handleBusinessException(exception);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("业务异常测试", result.getMessage());
    }

    @Test
    @DisplayName("处理业务异常 - 自定义错误码")
    void testHandleBusinessExceptionWithCode() {
        BusinessException exception = new BusinessException(401, "未授权访问");

        Result<Void> result = exceptionHandler.handleBusinessException(exception);

        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals("未授权访问", result.getMessage());
    }

    @Test
    @DisplayName("处理认证异常 - 密码错误")
    void testHandleAuthenticationException_BadCredentials() {
        BadCredentialsException exception = new BadCredentialsException("密码错误");

        Result<Void> result = exceptionHandler.handleAuthenticationException(exception);

        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());
    }

    @Test
    @DisplayName("处理权限不足异常")
    void testHandleAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("权限不足");

        Result<Void> result = exceptionHandler.handleAccessDeniedException(exception);

        assertNotNull(result);
        assertEquals(403, result.getCode());
        assertEquals("权限不足，无法访问", result.getMessage());
    }

    @Test
    @DisplayName("处理绑定异常")
    void testHandleBindException() {
        BindException exception = new BindException(new Object(), "object");
        exception.addError(new FieldError("object", "field", "字段验证失败"));

        Result<Void> result = exceptionHandler.handleBindException(exception);

        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertTrue(result.getMessage().contains("字段验证失败"));
    }

    @Test
    @DisplayName("处理一般异常")
    void testHandleException() {
        Exception exception = new RuntimeException("系统异常");

        Result<Void> result = exceptionHandler.handleException(exception);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("系统异常，请联系管理员", result.getMessage());
    }
}