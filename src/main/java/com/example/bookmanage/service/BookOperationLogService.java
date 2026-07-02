package com.example.bookmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.BookOperationLogQueryDTO;
import com.example.bookmanage.model.entity.BookOperationLog;
import com.example.bookmanage.model.enums.OperationType;
import com.example.bookmanage.model.vo.BookOperationLogVO;

/**
 * 操作日志 Service 接口
 */
public interface BookOperationLogService extends IService<BookOperationLog> {

    /**
     * 分页查询操作日志
     */
    PageResponse<BookOperationLogVO> getLogPage(BookOperationLogQueryDTO queryDTO);

    /**
     * 根据ID查询日志详情
     */
    BookOperationLogVO getLogById(Long id);

    /**
     * 记录操作日志（供其他 Service 内部调用，不暴露给 Controller）
     */
    void recordLog(Long bookId, Long operatorId, OperationType type,
                   String beforeData, String afterData, String remark);
}
