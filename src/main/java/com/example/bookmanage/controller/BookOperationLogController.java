package com.example.bookmanage.controller;

import com.example.bookmanage.common.response.ApiResponse;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.BookOperationLogQueryDTO;
import com.example.bookmanage.model.vo.BookOperationLogVO;
import com.example.bookmanage.service.BookOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 操作日志 Controller（只读）
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class BookOperationLogController {

    @Autowired
    private BookOperationLogService bookOperationLogService;

    @GetMapping("/operation-logs")
    public ApiResponse<PageResponse<BookOperationLogVO>> getLogPage(
            @Valid BookOperationLogQueryDTO queryDTO) {
        PageResponse<BookOperationLogVO> pageResponse = bookOperationLogService.getLogPage(queryDTO);
        return ApiResponse.success(pageResponse);
    }

    @GetMapping("/operation-logs/{id}")
    public ApiResponse<BookOperationLogVO> getLogById(@PathVariable Long id) {
        BookOperationLogVO vo = bookOperationLogService.getLogById(id);
        return ApiResponse.success(vo);
    }
}
