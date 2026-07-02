package com.example.bookmanage.controller;

import com.example.bookmanage.common.response.ApiResponse;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.BorrowRecordCreateDTO;
import com.example.bookmanage.model.dto.BorrowRecordQueryDTO;
import com.example.bookmanage.model.vo.BorrowRecordVO;
import com.example.bookmanage.service.BorrowRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 借阅记录 Controller
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @GetMapping("/borrow-records")
    public ApiResponse<PageResponse<BorrowRecordVO>> getBorrowRecordPage(
            @Valid BorrowRecordQueryDTO queryDTO) {
        PageResponse<BorrowRecordVO> pageResponse = borrowRecordService.getBorrowRecordPage(queryDTO);
        return ApiResponse.success(pageResponse);
    }

    @GetMapping("/borrow-records/{id}")
    public ApiResponse<BorrowRecordVO> getBorrowRecordById(@PathVariable Long id) {
        BorrowRecordVO vo = borrowRecordService.getBorrowRecordById(id);
        return ApiResponse.success(vo);
    }

    @PostMapping("/borrow-records")
    public ApiResponse<BorrowRecordVO> borrowBook(@Valid @RequestBody BorrowRecordCreateDTO dto) {
        BorrowRecordVO vo = borrowRecordService.borrowBook(dto);
        return ApiResponse.success(vo);
    }

    @PutMapping("/borrow-records/{id}/return")
    public ApiResponse<BorrowRecordVO> returnBook(@PathVariable Long id,
                                                   @RequestParam(required = false) String remark) {
        BorrowRecordVO vo = borrowRecordService.returnBook(id, remark);
        return ApiResponse.success(vo);
    }

    @PutMapping("/borrow-records/{id}/renew")
    public ApiResponse<BorrowRecordVO> renewBook(@PathVariable Long id) {
        BorrowRecordVO vo = borrowRecordService.renewBook(id);
        return ApiResponse.success(vo);
    }

    @PutMapping("/borrow-records/{id}/mark-lost")
    public ApiResponse<BorrowRecordVO> markLost(@PathVariable Long id,
                                                 @RequestParam(required = false) String remark) {
        BorrowRecordVO vo = borrowRecordService.markLost(id, remark);
        return ApiResponse.success(vo);
    }
}
