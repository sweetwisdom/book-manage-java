package com.example.bookmanage.model.dto;

/**
 * 用户DTO - 接收前端数据
 */
public class UserDTO {

    private String name;
    private Integer age;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
