package com.example.bookmanage.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 图书操作日志实体
 */
@Data
@Accessors(chain = true)
public class BookOperationLog {

    private Long id;
    private Long bookId;
    private Long operatorId;
    private String operationType;
    private String beforeData;
    private String afterData;
    private String remark;
    private LocalDateTime createTime;
}
