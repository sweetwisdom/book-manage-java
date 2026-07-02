package com.example.bookmanage.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图书库存 VO
 */
@Data
public class BookStockVO {

    private Long id;
    private Long bookId;

    /** 图书名称（关联查询填充） */
    private String bookTitle;

    private Integer totalCount;
    private Integer availableCount;
    private Integer borrowedCount;
    private Integer lostCount;
    private Integer damagedCount;
    private LocalDateTime updateTime;
}
