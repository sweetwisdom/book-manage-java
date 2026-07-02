package com.example.bookmanage.model.enums;

/**
 * 借阅状态枚举
 */
public enum BorrowStatus {

    BORROWED("BORROWED", "借阅中"),
    RETURNED("RETURNED", "已归还"),
    OVERDUE("OVERDUE", "已逾期"),
    LOST("LOST", "已丢失");

    private final String code;
    private final String description;

    BorrowStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static boolean isValid(String code) {
        if (code == null) {
            return false;
        }
        for (BorrowStatus status : values()) {
            if (status.code.equals(code)) {
                return true;
            }
        }
        return false;
    }
}
