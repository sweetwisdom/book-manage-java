package com.example.bookmanage.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录响应 VO
 */
@Data
@Accessors(chain = true)
public class LoginVO {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserVO user;
}
