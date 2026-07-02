package com.example.bookmanage.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户实体 - 对应数据库表
 */

@Data
@Accessors(chain = true)
public class User {

    private Long id;
    private String name;
    private Integer age;
    private String role;


}
