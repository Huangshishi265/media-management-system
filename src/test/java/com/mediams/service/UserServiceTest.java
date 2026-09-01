package com.mediams.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mediams.dto.LoginRequest;
import com.mediams.dto.LoginResponse;
import com.mediams.dto.PageResult;
import com.mediams.entity.User;
import com.mediams.mapper.UserMapper;
import com.mediams.service.impl.AuthServiceImpl;
import com.mediams.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import com.mediams.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .nickname("测试用户")
                .email("test@example.com")
                .status(1)
                .build();

        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    }

    @Test
    @DisplayName("根据用户名查询用户 - 成功")
    void testGetByUsername_Success() {
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        User result = userService.getByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getNickname());
    }

    @Test
    @DisplayName("根据用户名查询用户 - 用户不存在")
    void testGetByUsername_NotFound() {
        when(userMapper.selectByUsername("nonexistent")).thenReturn(null);

        User result = userService.getByUsername("nonexistent");

        assertNull(result);
    }

    @Test
    @DisplayName("创建用户 - 成功")
    void testCreateUser_Success() {
        User newUser = User.builder()
                .username("newuser")
                .password("password123")
                .nickname("新用户")
                .email("new@example.com")
                .build();

        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.selectByEmail("new@example.com")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User result = userService.createUser(newUser);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(1, result.getStatus());
    }

    @Test
    @DisplayName("分页查询用户 - 成功")
    void testPageList_Success() {
        // 测试分页查询功能
        // 这里需要模拟 MyBatis-Plus 的 Page 对象
        assertDoesNotThrow(() -> {
            userService.pageList(1, 10, null, null);
        });
    }

    @Test
    @DisplayName("更新用户状态 - 成功")
    void testUpdateStatus_Success() {
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        boolean result = userService.updateStatus(1L, 0);

        assertTrue(result);
    }

    @Test
    @DisplayName("删除用户 - 成功")
    void testDeleteUser_Success() {
        when(userMapper.deleteById(1L)).thenReturn(1);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
    }
}