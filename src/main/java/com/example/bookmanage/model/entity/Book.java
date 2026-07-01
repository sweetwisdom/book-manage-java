package com.example.bookmanage.model.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体 - 对应 book 表
 */
@Data
@Accessors(chain = true)
public class Book {

    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private Long categoryId;
    private String description;
    private String coverUrl;
    private Integer status;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
