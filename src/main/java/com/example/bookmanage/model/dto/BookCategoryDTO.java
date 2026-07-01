package com.example.bookmanage.model.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 图书分类DTO - 接收前端数据
 */
@Data
public class BookCategoryDTO {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Long parentId;

    private Integer sortOrder;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能为0或1")
    @Max(value = 1, message = "状态只能为0或1")
    private Integer status;
}
