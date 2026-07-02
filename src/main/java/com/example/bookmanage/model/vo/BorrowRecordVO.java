package com.example.bookmanage.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 借阅记录 VO
 */
@Data
public class BorrowRecordVO {

    private Long id;
    private Long userId;

    /** 用户名称（关联查询填充） */
    private String userName;

    private Long bookId;

    /** 图书名称（关联查询填充） */
    private String bookTitle;

    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    private String status;
    private Integer renewCount;
    private String remark;

    /** 是否逾期（动态计算） */
    private Boolean isOverdue;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
