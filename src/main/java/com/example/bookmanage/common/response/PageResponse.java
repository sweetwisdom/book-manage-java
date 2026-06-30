package com.example.bookmanage.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    /** 当前页数据 */
    private List<T> records;

    /** 总记录数 */
    private Long total;

    /** 当前页码 */
    private Long pageNum;

    /** 每页数量 */
    private Long pageSize;

    /** 总页数 */
    private Long pages;
}
