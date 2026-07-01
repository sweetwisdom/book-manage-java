package com.example.bookmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.BookDTO;
import com.example.bookmanage.model.dto.BookQueryDTO;
import com.example.bookmanage.model.entity.Book;
import com.example.bookmanage.model.vo.BookVO;

/**
 * 图书Service接口
 */
public interface BookService extends IService<Book> {

    /**
     * 分页查询图书
     */
    PageResponse<BookVO> getBookPage(BookQueryDTO queryDTO);

    /**
     * 根据ID获取图书详情
     */
    BookVO getBookById(Long id);

    /**
     * 新增图书
     */
    BookVO createBook(BookDTO dto);

    /**
     * 更新图书
     */
    BookVO updateBook(Long id, BookDTO dto);

    /**
     * 删除图书（逻辑删除）
     */
    void deleteBook(Long id);

    /**
     * 更新图书状态（上下架）
     */
    BookVO updateBookStatus(Long id, Integer status);
}
