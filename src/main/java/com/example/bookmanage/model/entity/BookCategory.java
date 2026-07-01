package com.example.bookmanage.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 图书分类实体 - 对应 book_category 表
 */
@Data
@Accessors(chain = true)
public class BookCategory {

    private Long id;
    private String name;
    private Long parentId;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
