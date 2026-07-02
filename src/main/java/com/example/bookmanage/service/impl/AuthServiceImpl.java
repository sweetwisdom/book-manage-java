package com.example.bookmanage.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bookmanage.common.auth.AuthTokenUtil;
import com.example.bookmanage.common.auth.AuthUser;
import com.example.bookmanage.common.config.AppProperties;
import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.common.exception.ErrorCode;
import com.example.bookmanage.model.dto.LoginDTO;
import com.example.bookmanage.model.entity.User;
import com.example.bookmanage.model.mapper.UserMapper;
import com.example.bookmanage.model.vo.LoginVO;
import com.example.bookmanage.model.vo.UserVO;
import com.example.bookmanage.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 认证 Service 实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final AuthTokenUtil authTokenUtil;
    private final AppProperties appProperties;

    public AuthServiceImpl(UserMapper userMapper, AuthTokenUtil authTokenUtil, AppProperties appProperties) {
        this.userMapper = userMapper;
        this.authTokenUtil = authTokenUtil;
        this.appProperties = appProperties;
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getName, loginDTO.getName())
                .last("LIMIT 1"));
        if (user == null || !StringUtils.hasText(user.getPasswordHash())
                || !BCrypt.checkpw(loginDTO.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        AuthUser authUser = new AuthUser(user.getId(), user.getName(), user.getRole());
        String token = authTokenUtil.createToken(authUser);
        return new LoginVO()
                .setToken(token)
                .setTokenType(appProperties.getAuth().getTokenPrefix().trim())
                .setExpiresIn(appProperties.getAuth().getExpireSeconds())
                .setUser(convertToVO(user));
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
