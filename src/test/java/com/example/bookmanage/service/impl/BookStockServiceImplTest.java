package com.example.bookmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.model.dto.BookStockAdjustDTO;
import com.example.bookmanage.model.entity.BookStock;
import com.example.bookmanage.model.mapper.BookMapper;
import com.example.bookmanage.model.mapper.BookStockMapper;
import com.example.bookmanage.model.vo.BookStockVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookStockServiceImplTest {

    @Mock
    private BookStockMapper bookStockMapper;

    @Mock
    private BookMapper bookMapper;

    private BookStockServiceImpl bookStockService;

    @BeforeEach
    void setUp() {
        bookStockService = new BookStockServiceImpl();
        ReflectionTestUtils.setField(bookStockService, "baseMapper", bookStockMapper);
        ReflectionTestUtils.setField(bookStockService, "bookMapper", bookMapper);
    }

    @Test
    void getStockById_ShouldReturnStock_WhenExists() {
        BookStock stock = new BookStock()
                .setId(1L).setBookId(100L)
                .setTotalCount(10).setAvailableCount(8)
                .setBorrowedCount(2).setLostCount(0).setDamagedCount(0);
        when(bookStockMapper.selectById(1L)).thenReturn(stock);

        BookStockVO result = bookStockService.getStockById(1L);

        assertThat(result.getBookId()).isEqualTo(100L);
        assertThat(result.getAvailableCount()).isEqualTo(8);
    }

    @Test
    void getStockById_ShouldThrowException_WhenNotFound() {
        when(bookStockMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> bookStockService.getStockById(999L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getStockByBookId_ShouldReturnStock_WhenExists() {
        BookStock stock = new BookStock()
                .setId(1L).setBookId(100L)
                .setTotalCount(5).setAvailableCount(5)
                .setBorrowedCount(0).setLostCount(0).setDamagedCount(0);
        when(bookStockMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(stock);

        BookStockVO result = bookStockService.getStockByBookId(100L);

        assertThat(result.getTotalCount()).isEqualTo(5);
    }

    @Test
    void adjustStock_IncreaseAvailable_ShouldSucceed() {
        BookStock stock = new BookStock()
                .setId(1L).setBookId(100L)
                .setTotalCount(10).setAvailableCount(8)
                .setBorrowedCount(2).setLostCount(0).setDamagedCount(0);
        when(bookStockMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(stock);
        when(bookStockMapper.updateById(any(BookStock.class))).thenReturn(1);

        BookStock updated = new BookStock()
                .setId(1L).setBookId(100L)
                .setTotalCount(13).setAvailableCount(11)
                .setBorrowedCount(2).setLostCount(0).setDamagedCount(0);
        when(bookStockMapper.selectById(1L)).thenReturn(updated);

        BookStockAdjustDTO dto = new BookStockAdjustDTO();
        dto.setAdjustCount(3);
        dto.setAdjustType("INCREASE");
        dto.setAdjustField("AVAILABLE");

        BookStockVO result = bookStockService.adjustStock(100L, dto);

        assertThat(result.getAvailableCount()).isEqualTo(11);
        assertThat(result.getTotalCount()).isEqualTo(13);
    }

    @Test
    void adjustStock_DecreaseExceedLimit_ShouldThrowException() {
        BookStock stock = new BookStock()
                .setId(1L).setBookId(100L)
                .setTotalCount(2).setAvailableCount(2)
                .setBorrowedCount(0).setLostCount(0).setDamagedCount(0);
        when(bookStockMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(stock);

        BookStockAdjustDTO dto = new BookStockAdjustDTO();
        dto.setAdjustCount(5);
        dto.setAdjustType("DECREASE");
        dto.setAdjustField("AVAILABLE");

        assertThatThrownBy(() -> bookStockService.adjustStock(100L, dto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void adjustStock_InvalidAdjustType_ShouldThrowException() {
        BookStock stock = new BookStock()
                .setId(1L).setBookId(100L)
                .setTotalCount(10).setAvailableCount(8)
                .setBorrowedCount(2).setLostCount(0).setDamagedCount(0);
        when(bookStockMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(stock);

        BookStockAdjustDTO dto = new BookStockAdjustDTO();
        dto.setAdjustCount(1);
        dto.setAdjustType("INVALID");
        dto.setAdjustField("TOTAL");

        assertThatThrownBy(() -> bookStockService.adjustStock(100L, dto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void initStock_ShouldCreateStockRecord() {
        when(bookStockMapper.insert(any(BookStock.class))).thenReturn(1);

        bookStockService.initStock(200L, 10);

        // 验证 insert 被调用，无异常抛出
    }

    @Test
    void decreaseAvailable_ShouldDecreaseCount() {
        BookStock stock = new BookStock()
                .setId(1L).setBookId(100L)
                .setTotalCount(10).setAvailableCount(5)
                .setBorrowedCount(5).setLostCount(0).setDamagedCount(0);
        when(bookStockMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(stock);
        when(bookStockMapper.updateById(any(BookStock.class))).thenReturn(1);

        bookStockService.decreaseAvailable(100L);

        assertThat(stock.getAvailableCount()).isEqualTo(4);
        assertThat(stock.getBorrowedCount()).isEqualTo(6);
    }

    @Test
    void increaseAvailable_ShouldIncreaseCount() {
        BookStock stock = new BookStock()
                .setId(1L).setBookId(100L)
                .setTotalCount(10).setAvailableCount(3)
                .setBorrowedCount(7).setLostCount(0).setDamagedCount(0);
        when(bookStockMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(stock);
        when(bookStockMapper.updateById(any(BookStock.class))).thenReturn(1);

        bookStockService.increaseAvailable(100L);

        assertThat(stock.getAvailableCount()).isEqualTo(4);
        assertThat(stock.getBorrowedCount()).isEqualTo(6);
    }
}
