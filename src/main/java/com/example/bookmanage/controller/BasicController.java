package com.example.bookmanage.controller;

import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.common.exception.ErrorCode;
import com.example.bookmanage.common.response.ApiResponse;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.UserDTO;
import com.example.bookmanage.model.dto.UserQueryDTO;
import com.example.bookmanage.model.vo.UserVO;
import com.example.bookmanage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 基础控制器 - 处理用户相关请求
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class BasicController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户
     * GET /api/users?pageNum=1&pageSize=10&name=xxx
     */
    @GetMapping("/users")
    public ApiResponse<PageResponse<UserVO>> getUsers(@Valid UserQueryDTO queryDTO) {
        log.info("分页查询用户: pageNum={}, pageSize={}, name={}",
                queryDTO.getPageNum(), queryDTO.getPageSize(), queryDTO.getName());
        return ApiResponse.success(userService.getUserPage(queryDTO));
    }

    /**
     * 根据ID获取用户
     * GET /api/users/{id}
     */
    @GetMapping("/users/{id}")
    public ApiResponse<UserVO> getUserById(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        if(user==null){
            throw  new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return ApiResponse.success(user);
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
