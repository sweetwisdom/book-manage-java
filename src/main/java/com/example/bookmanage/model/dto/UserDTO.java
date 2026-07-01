package com.example.bookmanage.model.dto;

import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;

/**
 * 用户DTO - 接收前端数据
 */
@Data
public class UserDTO {

    // Getters and Setters
    @NotBlank(message = "用户名不能为空")
    private String name;

    @Min(value = 0, message = "年龄不能小于0")
    private Integer age;

}
