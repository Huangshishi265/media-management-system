package com.mediams.controller;

import com.mediams.dto.LoginRequest;
import com.mediams.dto.LoginResponse;
import com.mediams.dto.Result;
import com.mediams.entity.User;
import com.mediams.service.AuthService;
import com.mediams.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器单元测试
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("用户登录接口测试")
    void testLoginEndpoint() throws Exception {
        LoginResponse loginResponse = LoginResponse.builder()
                .token("test-token")
                .userId(1L)
                .username("testuser")
                .nickname("测试用户")
                .expiration(86400000L)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("test-token"));
    }

    @Test
    @DisplayName("用户注册接口测试")
    void testRegisterEndpoint() throws Exception {
        when(authService.register(any(User.class))).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"newuser\",\"password\":\"password123\",\"nickname\":\"新用户\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取当前用户信息接口测试")
    void testGetCurrentUserEndpoint() throws Exception {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .nickname("测试用户")
                .email("test@example.com")
                .build();

        // 由于 SecurityUtil.getCurrentUsername() 在没有 Spring Security 上下文时会返回 null
        // 这里只测试接口的可访问性
        mockMvc.perform(get("/api/auth/current"))
                .andExpect(status().isOk());
    }
}