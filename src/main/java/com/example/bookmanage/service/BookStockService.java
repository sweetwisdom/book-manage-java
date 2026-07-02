package com.example.bookmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.BookStockAdjustDTO;
import com.example.bookmanage.model.entity.BookStock;
import com.example.bookmanage.model.vo.BookStockVO;

/**
 * 图书库存 Service 接口
 */
public interface BookStockService extends IService<BookStock> {

    /**
     * 分页查询库存列表
     */
    PageResponse<BookStockVO> getStockPage(Long pageNum, Long pageSize, Long bookId);

    /**
     * 根据图书ID查询库存
     */
    BookStockVO getStockByBookId(Long bookId);

    /**
     * 根据ID查询库存
     */
    BookStockVO getStockById(Long id);

    /**
     * 调整库存
     */
    BookStockVO adjustStock(Long bookId, BookStockAdjustDTO dto);

    /**
     * 初始化库存（创建图书时调用）
     */
    void initStock(Long bookId, Integer totalCount);

    /**
     * 扣减可借数量（借书时调用）
     */
    void decreaseAvailable(Long bookId);

    /**
     * 恢复可借数量（还书时调用）
     */
    void increaseAvailable(Long bookId);

    /**
     * 标记丢失时调整库存
     */
    void increaseLost(Long bookId);

    /**
     * 根据图书ID查询库存实体（内部使用，供其他Service调用）
     */
    BookStock getStockEntityByBookId(Long bookId);
}
