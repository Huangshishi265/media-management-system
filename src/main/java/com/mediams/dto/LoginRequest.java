package com.mediams.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 登录请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 验证码 */
    private String captcha;

    /** 验证码Key */
    private String captchaKey;
}