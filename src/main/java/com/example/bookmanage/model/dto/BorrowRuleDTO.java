package com.example.bookmanage.model.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 借阅规则 DTO
 */
@Data
public class BorrowRuleDTO {

    @NotBlank(message = "角色不能为空")
    private String role;

    @NotNull(message = "最大可借数量不能为空")
    @Min(value = 1, message = "最大可借数量必须大于0")
    private Integer maxBorrowCount;

    @NotNull(message = "最大借阅天数不能为空")
    @Min(value = 1, message = "最大借阅天数必须大于0")
    private Integer maxBorrowDays;

    @NotNull(message = "最大续借次数不能为空")
    @Min(value = 0, message = "最大续借次数不能小于0")
    private Integer maxRenewCount;

    @NotNull(message = "每日逾期费用不能为空")
    @Min(value = 0, message = "每日逾期费用不能小于0")
    private java.math.BigDecimal overdueFeePerDay;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能为0或1")
    @Max(value = 1, message = "状态只能为0或1")
    private Integer status;
}
