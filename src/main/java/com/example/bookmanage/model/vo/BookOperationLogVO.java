package com.example.bookmanage.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志 VO
 */
@Data
public class BookOperationLogVO {

    private Long id;
    private Long bookId;

    /** 图书名称（关联查询填充） */
    private String bookTitle;

    private Long operatorId;

    /** 操作人名称（关联查询填充） */
    private String operatorName;

    private String operationType;
    private String beforeData;
    private String afterData;
    private String remark;
    private LocalDateTime createTime;
}
