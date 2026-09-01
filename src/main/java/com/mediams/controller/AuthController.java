package com.mediams.controller;

import com.mediams.dto.LoginRequest;
import com.mediams.dto.LoginResponse;
import com.mediams.dto.Result;
import com.mediams.entity.User;
import com.mediams.service.AuthService;
import com.mediams.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        authService.register(user);
        return Result.success("注册成功");
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestHeader("Authorization") String token) {
        String newToken = token.replace("Bearer ", "");
        String refreshedToken = authService.refreshToken(newToken);
        return Result.success(refreshedToken);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        authService.logout(actualToken);
        return Result.success("退出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser() {
        String username = com.mediams.util.SecurityUtil.getCurrentUsername();
        if (username == null) {
            return Result.error(401, "未登录");
        }
        User user = userService.getByUsername(username);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return Result.success(user);
    }
}