package com.example.bookmanage.model.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 库存调整请求 DTO
 */
@Data
public class BookStockAdjustDTO {

    @NotNull(message = "调整数量不能为空")
    @Min(value = 1, message = "调整数量必须大于0")
    private Integer adjustCount;

    @NotBlank(message = "调整类型不能为空")
    private String adjustType;

    @NotBlank(message = "调整字段不能为空")
    private String adjustField;

    private String remark;
}
