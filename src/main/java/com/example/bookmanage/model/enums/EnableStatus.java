package com.example.bookmanage.model.enums;

/**
 * 通用启用状态
 */
public enum EnableStatus {

    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final int code;
    private final String description;

    EnableStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static boolean isValid(Integer code) {
        return code != null && (code == DISABLED.code || code == ENABLED.code);
    }
}
