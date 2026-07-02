package com.example.bookmanage.model.vo;

import lombok.Data;

/**
 * 用户VO - 返回给前端
 */
@Data
public class UserVO {

    private Long id;
    private String name;
    private Integer age;
    private String role;

}
