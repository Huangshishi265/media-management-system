package com.mediams.service;

import com.mediams.dto.LoginRequest;
import com.mediams.dto.LoginResponse;
import com.mediams.entity.User;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户注册
     */
    boolean register(User user);

    /**
     * 刷新 Token
     */
    String refreshToken(String token);

    /**
     * 退出登录
     */
    void logout(String token);
}
