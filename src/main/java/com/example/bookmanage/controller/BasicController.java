package com.example.bookmanage.controller;

import com.example.bookmanage.model.dto.UserDTO;
import com.example.bookmanage.service.UserService;
import com.example.bookmanage.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public List<UserVO> getAllUsers() {
        log.info("❤️用户新增");
        System.out.println("❤️❤️❤️"+ userService.count());
        return userService.getAllUsers();
    }

    /**
     * 根据ID获取用户
     * GET /api/users/{id}
     */
    @GetMapping("/users/{id}")
    public UserVO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * 创建用户
     * POST /api/users
     */
    @PostMapping("/users")
    public UserVO createUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }
}
