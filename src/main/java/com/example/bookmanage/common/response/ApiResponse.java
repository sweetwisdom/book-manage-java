package com.example.bookmanage.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应格式
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    /** 状态码 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 响应数据 */
    private T data;

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

    /**
     * 失败响应（自定义状态码）
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
