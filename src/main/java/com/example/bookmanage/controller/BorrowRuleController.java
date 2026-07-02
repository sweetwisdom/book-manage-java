package com.example.bookmanage.controller;

import com.example.bookmanage.common.response.ApiResponse;
import com.example.bookmanage.model.dto.BorrowRuleDTO;
import com.example.bookmanage.model.vo.BorrowRuleVO;
import com.example.bookmanage.service.BorrowRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 借阅规则 Controller
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class BorrowRuleController {

    @Autowired
    private BorrowRuleService borrowRuleService;

    @GetMapping("/borrow-rules")
    public ApiResponse<List<BorrowRuleVO>> getRuleList(
            @RequestParam(required = false) String role) {
        List<BorrowRuleVO> list = borrowRuleService.getRuleList(role);
        return ApiResponse.success(list);
    }

    @GetMapping("/borrow-rules/{id}")
    public ApiResponse<BorrowRuleVO> getRuleById(@PathVariable Long id) {
        BorrowRuleVO vo = borrowRuleService.getRuleById(id);
        return ApiResponse.success(vo);
    }

    @PostMapping("/borrow-rules")
    public ApiResponse<BorrowRuleVO> createRule(@Valid @RequestBody BorrowRuleDTO dto) {
        BorrowRuleVO vo = borrowRuleService.createRule(dto);
        return ApiResponse.success(vo);
    }

    @PutMapping("/borrow-rules/{id}")
    public ApiResponse<BorrowRuleVO> updateRule(@PathVariable Long id,
                                                 @Valid @RequestBody BorrowRuleDTO dto) {
        BorrowRuleVO vo = borrowRuleService.updateRule(id, dto);
        return ApiResponse.success(vo);
    }

    @DeleteMapping("/borrow-rules/{id}")
    public ApiResponse<Void> deleteRule(@PathVariable Long id) {
        borrowRuleService.deleteRule(id);
        return ApiResponse.success();
    }

    @PutMapping("/borrow-rules/{id}/status")
    public ApiResponse<BorrowRuleVO> updateRuleStatus(
            @PathVariable Long id,
            @RequestParam @NotNull(message = "状态不能为空")
            @Min(value = 0, message = "状态只能为0或1")
            @Max(value = 1, message = "状态只能为0或1") Integer status) {
        BorrowRuleVO vo = borrowRuleService.updateRuleStatus(id, status);
        return ApiResponse.success(vo);
    }
}
