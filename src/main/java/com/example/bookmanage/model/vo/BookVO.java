package com.example.bookmanage.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书VO - 返回给前端
 */
@Data
public class BookVO {

    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private Long categoryId;

    /** 分类名称（关联查询填充） */
    private String categoryName;

    private String description;
    private String coverUrl;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
