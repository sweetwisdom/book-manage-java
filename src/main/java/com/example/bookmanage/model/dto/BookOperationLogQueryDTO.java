package com.example.bookmanage.model.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 操作日志分页查询 DTO
 */
@Data
public class BookOperationLogQueryDTO {

    @Min(value = 1, message = "页码不能小于1")
    private Long pageNum = 1L;

    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能大于100")
    private Long pageSize = 10L;

    private Long bookId;

    private Long operatorId;

    private String operationType;
}
