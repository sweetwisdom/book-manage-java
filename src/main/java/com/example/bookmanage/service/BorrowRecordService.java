package com.example.bookmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.BorrowRecordCreateDTO;
import com.example.bookmanage.model.dto.BorrowRecordQueryDTO;
import com.example.bookmanage.model.entity.BorrowRecord;
import com.example.bookmanage.model.vo.BorrowRecordVO;

/**
 * 借阅记录 Service 接口
 */
public interface BorrowRecordService extends IService<BorrowRecord> {

    /**
     * 分页查询借阅记录
     */
    PageResponse<BorrowRecordVO> getBorrowRecordPage(BorrowRecordQueryDTO queryDTO);

    /**
     * 根据ID查询借阅记录
     */
    BorrowRecordVO getBorrowRecordById(Long id);

    /**
     * 借书
     */
    BorrowRecordVO borrowBook(BorrowRecordCreateDTO dto);

    /**
     * 还书
     */
    BorrowRecordVO returnBook(Long id, String remark);

    /**
     * 续借
     */
    BorrowRecordVO renewBook(Long id);

    /**
     * 标记丢失
     */
    BorrowRecordVO markLost(Long id, String remark);
}
