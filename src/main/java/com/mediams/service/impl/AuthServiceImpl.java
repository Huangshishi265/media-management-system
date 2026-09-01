package com.mediams.service.impl;

import com.mediams.dto.LoginRequest;
import com.mediams.dto.LoginResponse;
import com.mediams.entity.User;
import com.mediams.exception.BusinessException;
import com.mediams.mapper.UserMapper;
import com.mediams.service.AuthService;
import com.mediams.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_CACHE_PREFIX = "auth:token:";
    private static final String USER_CACHE_PREFIX = "auth:user:";

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }

        // 校验密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "密码错误");
        }

        // 校验状态
        if (user.getStatus() != 1) {
            throw new BusinessException(403, "用户已被禁用");
        }

        // 生成 Token
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());

        // 缓存用户信息
        redisTemplate.opsForValue().set(USER_CACHE_PREFIX + user.getId(), user, 24, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(TOKEN_CACHE_PREFIX + token, user.getId(), 24, TimeUnit.HOURS);

        log.info("用户登录成功: {}", user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .expiration(86400000L)
                .build();
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setGender(0);

        userMapper.insert(user);
        return true;
    }

    @Override
    public String refreshToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("Token 为空");
        }

        String newToken = jwtUtil.refreshToken(token);
        return newToken;
    }

    @Override
    public void logout(String token) {
        // 删除 Token 缓存
        if (StringUtils.hasText(token)) {
            Long userId = jwtUtil.getUserIdFromToken(token);
            redisTemplate.delete(TOKEN_CACHE_PREFIX + token);
            redisTemplate.delete(USER_CACHE_PREFIX + userId);
            log.info("用户退出登录: userId={}", userId);
        }
    }
}