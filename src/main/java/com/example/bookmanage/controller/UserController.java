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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 用户管理 Controller
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户
     * GET /api/users?pageNum=1&pageSize=10&name=xxx
     */
    @GetMapping("/users")
    public ApiResponse<PageResponse<UserVO>> getUserPage(@Valid UserQueryDTO queryDTO) {
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
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return ApiResponse.success(user);
    }

    /**
     * 创建用户
     * POST /api/users
     */
    @PostMapping("/users")
    public ApiResponse<UserVO> createUser(@Valid @RequestBody UserDTO dto) {
        log.info("创建用户: {}", dto.getName());
        return ApiResponse.success(userService.createUser(dto));
    }

    /**
     * 更新用户
     * PUT /api/users/{id}
     */
    @PutMapping("/users/{id}")
    public ApiResponse<UserVO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        UserVO user = userService.updateUser(id, dto);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        log.info("更新用户: id={}, name={}", id, dto.getName());
        return ApiResponse.success(user);
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        log.info("删除用户: id={}", id);
        return ApiResponse.success();
    }
}
