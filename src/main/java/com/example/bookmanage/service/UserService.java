package com.example.bookmanage.service;

import com.example.bookmanage.dto.UserDTO;
import com.example.bookmanage.entity.User;
import com.example.bookmanage.mapper.UserMapper;
import com.example.bookmanage.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户Service - 业务逻辑
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取所有用户
     */
    public List<UserVO> getAllUsers() {
        List<User> users = userMapper.findAll();
        return users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取用户
     */
    public UserVO getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            return null;
        }
        return convertToVO(user);
    }

    /**
     * 创建用户
     */
    public UserVO createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setAge(userDTO.getAge());
        userMapper.insert(user);
        return convertToVO(user);
    }

    /**
     * Entity -> VO 转换
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setName(user.getName());
        vo.setAge(user.getAge());
        return vo;
    }
}
