package com.example.bookmanage.controller;

import com.example.bookmanage.common.response.ApiResponse;
import com.example.bookmanage.model.dto.LoginDTO;
import com.example.bookmanage.model.vo.LoginVO;
import com.example.bookmanage.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 认证 Controller
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return ApiResponse.success(authService.login(loginDTO));
    }
}
