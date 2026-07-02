package com.example.bookmanage.controller;

import com.example.bookmanage.common.response.ApiResponse;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.BookStockAdjustDTO;
import com.example.bookmanage.model.vo.BookStockVO;
import com.example.bookmanage.service.BookStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 图书库存 Controller
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class BookStockController {

    @Autowired
    private BookStockService bookStockService;

    @GetMapping("/book-stocks")
    public ApiResponse<PageResponse<BookStockVO>> getStockPage(
            @RequestParam(required = false, defaultValue = "1")
            @Min(value = 1, message = "页码不能小于1") Long pageNum,
            @RequestParam(required = false, defaultValue = "10")
            @Min(value = 1, message = "每页数量不能小于1")
            @Max(value = 100, message = "每页数量不能大于100") Long pageSize,
            @RequestParam(required = false) Long bookId) {
        PageResponse<BookStockVO> pageResponse = bookStockService.getStockPage(pageNum, pageSize, bookId);
        return ApiResponse.success(pageResponse);
    }

    @GetMapping("/book-stocks/{id}")
    public ApiResponse<BookStockVO> getStockById(@PathVariable Long id) {
        BookStockVO vo = bookStockService.getStockById(id);
        return ApiResponse.success(vo);
    }

    @GetMapping("/book-stocks/book/{bookId}")
    public ApiResponse<BookStockVO> getStockByBookId(@PathVariable Long bookId) {
        BookStockVO vo = bookStockService.getStockByBookId(bookId);
        return ApiResponse.success(vo);
    }

    @PutMapping("/book-stocks/book/{bookId}/adjust")
    public ApiResponse<BookStockVO> adjustStock(@PathVariable Long bookId,
                                                 @Valid @RequestBody BookStockAdjustDTO dto) {
        BookStockVO vo = bookStockService.adjustStock(bookId, dto);
        return ApiResponse.success(vo);
    }
}
