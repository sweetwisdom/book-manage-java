package com.example.bookmanage.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 借阅记录实体
 */
@Data
@Accessors(chain = true)
public class BorrowRecord {

    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    private String status;
    private Integer renewCount;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
