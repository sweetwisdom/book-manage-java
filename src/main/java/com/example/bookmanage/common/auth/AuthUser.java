package com.example.bookmanage.common.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {

    private Long userId;
    private String name;
    private String role;
}
