package com.example.bookmanage.common.exception;

/**
 * 统一错误码
 */
public enum ErrorCode {

    SUCCESS(200, "success"),
    PARAM_ERROR(400, "参数错误"),
    BUSINESS_ERROR(400, "业务处理失败"),
    USER_NOT_FOUND(404, "用户不存在"),
    SYSTEM_ERROR(500, "系统异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
