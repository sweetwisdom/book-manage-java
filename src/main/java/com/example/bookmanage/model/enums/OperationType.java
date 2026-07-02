package com.example.bookmanage.model.enums;

/**
 * 操作类型枚举
 */
public enum OperationType {

    CREATE_BOOK("CREATE_BOOK", "新增图书"),
    UPDATE_BOOK("UPDATE_BOOK", "修改图书"),
    DELETE_BOOK("DELETE_BOOK", "删除图书"),
    ADJUST_STOCK("ADJUST_STOCK", "调整库存"),
    BORROW_BOOK("BORROW_BOOK", "借出图书"),
    RETURN_BOOK("RETURN_BOOK", "归还图书"),
    MARK_LOST("MARK_LOST", "标记丢失"),
    MARK_DAMAGED("MARK_DAMAGED", "标记损坏");

    private final String code;
    private final String description;

    OperationType(String code, String description) {
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
        for (OperationType type : values()) {
            if (type.code.equals(code)) {
                return true;
            }
        }
        return false;
    }
}
