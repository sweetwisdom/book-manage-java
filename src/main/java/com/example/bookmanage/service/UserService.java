package com.example.bookmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.UserDTO;
import com.example.bookmanage.model.dto.UserQueryDTO;
import com.example.bookmanage.model.entity.User;
import com.example.bookmanage.model.vo.UserVO;

import java.util.List;

/**
 * 用户Service接口 - 定义业务契约
 */
public interface UserService extends IService<User> {

    /**
     * 获取所有用户
     */
    List<UserVO> getAllUsers();

    /**
     * 分页查询用户
     */
    PageResponse<UserVO> getUserPage(UserQueryDTO queryDTO);

    /**
     * 根据ID获取用户
     */
    UserVO getUserById(Long id);

    /**
     * 创建用户
     */
    UserVO createUser(UserDTO userDTO);

    /**
     * 更新用户
     */
    UserVO updateUser(Long id, UserDTO userDTO);

    /**
     * 删除用户
     */
    void deleteUser(Long id);
}
