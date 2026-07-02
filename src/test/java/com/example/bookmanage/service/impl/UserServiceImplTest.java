package com.example.bookmanage.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.example.bookmanage.model.dto.UserDTO;
import com.example.bookmanage.model.entity.User;
import com.example.bookmanage.model.mapper.UserMapper;
import com.example.bookmanage.model.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        lenient().when(userMapper.insert(any(User.class))).thenReturn(1);
        lenient().when(userMapper.updateById(any(User.class))).thenReturn(1);
    }

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

    @Test
    void createUser_ShouldHashPassword_WhenPasswordProvided() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("admin");
        userDTO.setAge(30);
        userDTO.setRole("ADMIN");
        userDTO.setPassword("123456");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        UserVO result = userService.createUser(userDTO);

        verify(userMapper).insert(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(result.getName()).isEqualTo("admin");
        assertThat(savedUser.getPasswordHash()).isNotBlank();
        assertThat(savedUser.getPasswordHash()).isNotEqualTo("123456");
        assertThat(BCrypt.checkpw("123456", savedUser.getPasswordHash())).isTrue();
    }

    @Test
    void updateUser_ShouldHashPassword_WhenPasswordProvided() {
        User existing = new User().setId(1L).setName("admin").setPasswordHash(BCrypt.hashpw("oldPassword"));
        User updated = new User()
                .setId(1L)
                .setName("admin")
                .setAge(31)
                .setRole("ADMIN")
                .setPasswordHash(BCrypt.hashpw("newPassword"));
        when(userMapper.selectById(1L)).thenReturn(existing, updated);
        UserDTO userDTO = new UserDTO();
        userDTO.setName("admin");
        userDTO.setAge(31);
        userDTO.setRole("ADMIN");
        userDTO.setPassword("newPassword");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        UserVO result = userService.updateUser(1L, userDTO);

        verify(userMapper).updateById(userCaptor.capture());
        User updateUser = userCaptor.getValue();
        assertThat(result.getAge()).isEqualTo(31);
        assertThat(updateUser.getPasswordHash()).isNotBlank();
        assertThat(updateUser.getPasswordHash()).isNotEqualTo("newPassword");
        assertThat(BCrypt.checkpw("newPassword", updateUser.getPasswordHash())).isTrue();
    }

    @Test
    void updateUser_ShouldKeepPassword_WhenPasswordBlank() {
        User existing = new User().setId(1L).setName("admin").setPasswordHash(BCrypt.hashpw("oldPassword"));
        User updated = new User().setId(1L).setName("admin").setAge(31).setRole("ADMIN").setPasswordHash(existing.getPasswordHash());
        when(userMapper.selectById(1L)).thenReturn(existing, updated);
        UserDTO userDTO = new UserDTO();
        userDTO.setName("admin");
        userDTO.setAge(31);
        userDTO.setRole("ADMIN");
        userDTO.setPassword("");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.updateUser(1L, userDTO);

        verify(userMapper).updateById(userCaptor.capture());
        assertThat(userCaptor.getValue().getPasswordHash()).isNull();
    }
}
