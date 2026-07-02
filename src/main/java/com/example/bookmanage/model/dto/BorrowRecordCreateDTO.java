package com.example.bookmanage.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 借书请求 DTO
 */
@Data
public class BorrowRecordCreateDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "图书ID不能为空")
    private Long bookId;

    private String remark;
}
