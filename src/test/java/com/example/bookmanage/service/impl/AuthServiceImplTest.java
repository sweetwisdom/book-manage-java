package com.example.bookmanage.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.example.bookmanage.common.auth.AuthTokenUtil;
import com.example.bookmanage.common.config.AppProperties;
import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.common.exception.ErrorCode;
import com.example.bookmanage.model.dto.LoginDTO;
import com.example.bookmanage.model.entity.User;
import com.example.bookmanage.model.mapper.UserMapper;
import com.example.bookmanage.model.vo.LoginVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        AppProperties appProperties = new AppProperties();
        appProperties.getAuth().setSecret("test-secret");
        appProperties.getAuth().setExpireSeconds(7200L);
        AuthTokenUtil authTokenUtil = new AuthTokenUtil(appProperties);
        authService = new AuthServiceImpl(userMapper, authTokenUtil, appProperties);
    }

    @Test
    void login_ShouldReturnToken_WhenPasswordValid() {
        User user = new User()
                .setId(1L)
                .setName("admin")
                .setAge(30)
                .setRole("ADMIN")
                .setPasswordHash(BCrypt.hashpw("123456"));
        when(userMapper.selectOne(any())).thenReturn(user);

        LoginVO result = authService.login(createLoginDTO("admin", "123456"));

        assertThat(result.getToken()).isNotBlank();
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getExpiresIn()).isEqualTo(7200L);
        assertThat(result.getUser().getName()).isEqualTo("admin");
        assertThat(result.getUser().getRole()).isEqualTo("ADMIN");
    }

    @Test
    void login_ShouldThrowBusinessException_WhenPasswordInvalid() {
        User user = new User()
                .setId(1L)
                .setName("admin")
                .setPasswordHash(BCrypt.hashpw("123456"));
        when(userMapper.selectOne(any())).thenReturn(user);

        assertThatThrownBy(() -> authService.login(createLoginDTO("admin", "wrong")))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.LOGIN_FAILED.getCode());
    }

    @Test
    void login_ShouldThrowBusinessException_WhenUserNotFound() {
        when(userMapper.selectOne(any())).thenReturn(null);

        assertThatThrownBy(() -> authService.login(createLoginDTO("admin", "123456")))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.LOGIN_FAILED.getCode());
    }

    private LoginDTO createLoginDTO(String name, String password) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setName(name);
        loginDTO.setPassword(password);
        return loginDTO;
    }
}
