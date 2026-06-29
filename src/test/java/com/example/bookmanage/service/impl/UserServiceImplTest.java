package com.example.bookmanage.service.impl;

import com.example.bookmanage.model.entity.User;
import com.example.bookmanage.model.mapper.UserMapper;
import com.example.bookmanage.model.vo.UserVO;
import com.example.bookmanage.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUsersById() {
        User user = new User();
        user.setId(1L).setName("zcc").setAge(12);
        when(userMapper.selectById(1L))
                .thenReturn(user);
        UserVO result = userService.getUserById(1L);
        assertThat(
                result.getName()).isEqualTo("zcc");


    }
}