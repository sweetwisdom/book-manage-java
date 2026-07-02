package com.example.bookmanage.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借阅规则实体
 */
@Data
@Accessors(chain = true)
public class BorrowRule {

    private Long id;
    private String role;
    private Integer maxBorrowCount;
    private Integer maxBorrowDays;
    private Integer maxRenewCount;
    private BigDecimal overdueFeePerDay;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
