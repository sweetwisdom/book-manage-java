package com.example.bookmanage.service;

import com.example.bookmanage.model.dto.LoginDTO;
import com.example.bookmanage.model.vo.LoginVO;

/**
 * 认证 Service 接口
 */
public interface AuthService {

    /**
     * 用户登录并签发 token
     */
    LoginVO login(LoginDTO loginDTO);
}
