package com.example.bookmanage.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 图书分类VO - 返回给前端，支持树形结构
 */
@Data
public class BookCategoryVO {

    private Long id;
    private String name;
    private Long parentId;
    private Integer sortOrder;
    private Integer status;

    /** 子分类列表（树形结构时使用） */
    private List<BookCategoryVO> children;
}
