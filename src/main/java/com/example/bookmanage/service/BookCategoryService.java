package com.example.bookmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.bookmanage.model.dto.BookCategoryDTO;
import com.example.bookmanage.model.entity.BookCategory;
import com.example.bookmanage.model.vo.BookCategoryVO;

import java.util.List;

/**
 * 图书分类Service接口
 */
public interface BookCategoryService extends IService<BookCategory> {

    /**
     * 获取分类列表（支持平铺和树形）
     */
    List<BookCategoryVO> getCategories(Long parentId, Boolean tree);

    /**
     * 根据ID获取分类
     */
    BookCategoryVO getCategoryById(Long id);

    /**
     * 新增分类
     */
    BookCategoryVO createCategory(BookCategoryDTO dto);

    /**
     * 更新分类
     */
    BookCategoryVO updateCategory(Long id, BookCategoryDTO dto);

    /**
     * 删除分类（检查子分类和关联图书）
     */
    void deleteCategory(Long id);
}
