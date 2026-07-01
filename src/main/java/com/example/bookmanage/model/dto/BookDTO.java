package com.example.bookmanage.model.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 图书DTO - 接收前端数据
 */
@Data
public class BookDTO {

    private String isbn;

    @NotBlank(message = "书名不能为空")
    private String title;

    private String author;

    private String publisher;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "出版日期格式必须为 yyyy-MM-dd")
    private String publishDate;

    private Long categoryId;

    private String description;

    private String coverUrl;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能为0或1")
    @Max(value = 1, message = "状态只能为0或1")
    private Integer status;
}
