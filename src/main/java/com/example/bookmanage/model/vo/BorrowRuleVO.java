package com.example.bookmanage.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借阅规则 VO
 */
@Data
public class BorrowRuleVO {

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
