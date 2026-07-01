package com.example.bookmanage.controller;

import com.example.bookmanage.common.response.ApiResponse;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.BookDTO;
import com.example.bookmanage.model.dto.BookQueryDTO;
import com.example.bookmanage.model.vo.BookVO;
import com.example.bookmanage.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 图书控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 分页查询图书
     * GET /api/books?pageNum=1&pageSize=10&title=xxx&categoryId=1&status=1
     */
    @GetMapping("/books")
    public ApiResponse<PageResponse<BookVO>> getBooks(@Valid BookQueryDTO queryDTO) {
        log.info("分页查询图书: pageNum={}, pageSize={}, title={}, categoryId={}, status={}",
                queryDTO.getPageNum(), queryDTO.getPageSize(),
                queryDTO.getTitle(), queryDTO.getCategoryId(), queryDTO.getStatus());
        return ApiResponse.success(bookService.getBookPage(queryDTO));
    }

    /**
     * 根据ID获取图书详情
     * GET /api/books/{id}
     */
    @GetMapping("/books/{id}")
    public ApiResponse<BookVO> getBookById(@PathVariable Long id) {
        log.info("查询图书详情: id={}", id);
        return ApiResponse.success(bookService.getBookById(id));
    }

    /**
     * 新增图书
     * POST /api/books
     */
    @PostMapping("/books")
    public ApiResponse<BookVO> createBook(@Valid @RequestBody BookDTO dto) {
        log.info("新增图书: title={}, isbn={}", dto.getTitle(), dto.getIsbn());
        return ApiResponse.success(bookService.createBook(dto));
    }

    /**
     * 更新图书
     * PUT /api/books/{id}
     */
    @PutMapping("/books/{id}")
    public ApiResponse<BookVO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO dto) {
        log.info("更新图书: id={}, title={}", id, dto.getTitle());
        return ApiResponse.success(bookService.updateBook(id, dto));
    }

    /**
     * 删除图书（逻辑删除）
     * DELETE /api/books/{id}
     */
    @DeleteMapping("/books/{id}")
    public ApiResponse<Void> deleteBook(@PathVariable Long id) {
        log.info("删除图书: id={}", id);
        bookService.deleteBook(id);
        return ApiResponse.success();
    }

    /**
     * 更新图书状态（上下架）
     * PUT /api/books/{id}/status?status=0
     */
    @PutMapping("/books/{id}/status")
    public ApiResponse<BookVO> updateBookStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        log.info("更新图书状态: id={}, status={}", id, status);
        return ApiResponse.success(bookService.updateBookStatus(id, status));
    }
}
