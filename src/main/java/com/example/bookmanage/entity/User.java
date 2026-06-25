package com.example.bookmanage.entity;

import lombok.Data;

/**
 * 用户实体 - 对应数据库表
 */

@Data
public class User {

    private Long id;
    private String name;
    private Integer age;


}
