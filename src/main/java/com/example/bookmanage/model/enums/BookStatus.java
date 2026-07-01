package com.example.bookmanage.model.enums;

/**
 * 图书状态
 */
public enum BookStatus {

    OFF_SHELF(0, "下架"),
    ON_SHELF(1, "上架");

    private final int code;
    private final String description;

    BookStatus(int code, String description) {
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
        return code != null && (code == OFF_SHELF.code || code == ON_SHELF.code);
    }
}
