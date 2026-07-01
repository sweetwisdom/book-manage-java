package com.example.bookmanage.controller;

import com.example.bookmanage.common.response.ApiResponse;
import com.example.bookmanage.model.dto.BookCategoryDTO;
import com.example.bookmanage.model.vo.BookCategoryVO;
import com.example.bookmanage.service.BookCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 图书分类控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class BookCategoryController {

    @Autowired
    private BookCategoryService bookCategoryService;

    /**
     * 获取分类列表（支持平铺和树形）
     * GET /api/categories?parentId=0&tree=true
     */
    @GetMapping("/categories")
    public ApiResponse<List<BookCategoryVO>> getCategories(
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "false") Boolean tree) {
        log.info("查询分类列表: parentId={}, tree={}", parentId, tree);
        return ApiResponse.success(bookCategoryService.getCategories(parentId, tree));
    }

    /**
     * 根据ID获取分类
     * GET /api/categories/{id}
     */
    @GetMapping("/categories/{id}")
    public ApiResponse<BookCategoryVO> getCategoryById(@PathVariable Long id) {
        log.info("查询分类详情: id={}", id);
        return ApiResponse.success(bookCategoryService.getCategoryById(id));
    }

    /**
     * 新增分类
     * POST /api/categories
     */
    @PostMapping("/categories")
    public ApiResponse<BookCategoryVO> createCategory(@Valid @RequestBody BookCategoryDTO dto) {
        log.info("新增分类: name={}, parentId={}", dto.getName(), dto.getParentId());
        return ApiResponse.success(bookCategoryService.createCategory(dto));
    }

    /**
     * 更新分类
     * PUT /api/categories/{id}
     */
    @PutMapping("/categories/{id}")
    public ApiResponse<BookCategoryVO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody BookCategoryDTO dto) {
        log.info("更新分类: id={}, name={}", id, dto.getName());
        return ApiResponse.success(bookCategoryService.updateCategory(id, dto));
    }

    /**
     * 删除分类
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/categories/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        log.info("删除分类: id={}", id);
        bookCategoryService.deleteCategory(id);
        return ApiResponse.success();
    }
}
