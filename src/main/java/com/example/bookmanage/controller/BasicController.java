package com.example.bookmanage.controller;

import com.example.bookmanage.common.response.ApiResponse;
import com.example.bookmanage.model.dto.UserDTO;
import com.example.bookmanage.model.vo.UserVO;
import com.example.bookmanage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

/**
 * 基础控制器 - 处理用户相关请求
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class BasicController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户
     * GET /api/users
     */
    @GetMapping("/users")
    public ApiResponse<List<UserVO>> getAllUsers() {
        log.info("获取所有用户");
        return ApiResponse.success(userService.getAllUsers());
    }

    /**
     * 根据ID获取用户
     * GET /api/users/{id}
     */
    @GetMapping("/users/{id}")
    public ApiResponse<UserVO> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    /**
     * 创建用户
     * POST /api/users
     */
    @PostMapping("/users")
    public ApiResponse<UserVO> createUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("创建用户: {}", userDTO.getName());
        return ApiResponse.success(userService.createUser(userDTO));
    }
}
