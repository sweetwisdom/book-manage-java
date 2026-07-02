package com.example.bookmanage.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 图书库存实体
 */
@Data
@Accessors(chain = true)
public class BookStock {

    private Long id;
    private Long bookId;
    private Integer totalCount;
    private Integer availableCount;
    private Integer borrowedCount;
    private Integer lostCount;
    private Integer damagedCount;
    private LocalDateTime updateTime;
}
