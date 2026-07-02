package com.example.bookmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.model.dto.BorrowRecordCreateDTO;
import com.example.bookmanage.model.entity.Book;
import com.example.bookmanage.model.entity.BookStock;
import com.example.bookmanage.model.entity.BorrowRecord;
import com.example.bookmanage.model.entity.BorrowRule;
import com.example.bookmanage.model.entity.User;
import com.example.bookmanage.model.enums.BorrowStatus;
import com.example.bookmanage.model.mapper.BookMapper;
import com.example.bookmanage.model.mapper.BorrowRecordMapper;
import com.example.bookmanage.model.mapper.UserMapper;
import com.example.bookmanage.model.vo.BorrowRecordVO;
import com.example.bookmanage.service.BookOperationLogService;
import com.example.bookmanage.service.BookStockService;
import com.example.bookmanage.service.BorrowRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowRecordServiceImplTest {

    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BookStockService bookStockService;

    @Mock
    private BorrowRuleService borrowRuleService;

    @Mock
    private BookOperationLogService bookOperationLogService;

    private BorrowRecordServiceImpl borrowRecordService;

    @BeforeEach
    void setUp() {
        borrowRecordService = new BorrowRecordServiceImpl();
        ReflectionTestUtils.setField(borrowRecordService, "baseMapper", borrowRecordMapper);
        ReflectionTestUtils.setField(borrowRecordService, "bookMapper", bookMapper);
        ReflectionTestUtils.setField(borrowRecordService, "userMapper", userMapper);
        ReflectionTestUtils.setField(borrowRecordService, "bookStockService", bookStockService);
        ReflectionTestUtils.setField(borrowRecordService, "borrowRuleService", borrowRuleService);
        ReflectionTestUtils.setField(borrowRecordService, "bookOperationLogService", bookOperationLogService);
    }

    @Test
    void borrowBook_ShouldSucceed() {
        User user = new User().setId(1L).setName("张三").setRole("DEFAULT");
        Book book = new Book().setId(100L).setTitle("测试图书").setStatus(1);
        BorrowRule rule = new BorrowRule()
                .setId(1L).setRole("DEFAULT")
                .setMaxBorrowCount(5).setMaxBorrowDays(30)
                .setMaxRenewCount(1)
                .setOverdueFeePerDay(BigDecimal.valueOf(0.50))
                .setStatus(1);
        BookStock stock = new BookStock()
                .setId(1L).setBookId(100L)
                .setTotalCount(10).setAvailableCount(5)
                .setBorrowedCount(5).setLostCount(0).setDamagedCount(0);

        BorrowRecordCreateDTO dto = new BorrowRecordCreateDTO();
        dto.setUserId(1L);
        dto.setBookId(100L);

        when(userMapper.selectById(1L)).thenReturn(user);
        when(bookMapper.selectById(100L)).thenReturn(book);
        when(borrowRuleService.getRuleByRole("DEFAULT")).thenReturn(rule);
        when(borrowRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(bookStockService.getStockEntityByBookId(100L)).thenReturn(stock);
        when(borrowRecordMapper.insert(any(BorrowRecord.class))).thenReturn(1);

        BorrowRecordVO result = borrowRecordService.borrowBook(dto);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getBookId()).isEqualTo(100L);
        assertThat(result.getStatus()).isEqualTo(BorrowStatus.BORROWED.getCode());
        verify(bookStockService).decreaseAvailable(100L);
        verify(bookOperationLogService).recordLog(anyLong(), anyLong(), any(), any(), anyString(), any());
    }

    @Test
    void borrowBook_ShouldThrowException_WhenUserNotFound() {
        BorrowRecordCreateDTO dto = new BorrowRecordCreateDTO();
        dto.setUserId(999L);
        dto.setBookId(100L);

        when(userMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> borrowRecordService.borrowBook(dto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void borrowBook_ShouldThrowException_WhenBookNotAvailable() {
        User user = new User().setId(1L).setName("张三").setRole("DEFAULT");
        Book book = new Book().setId(100L).setTitle("下架图书").setStatus(0);

        BorrowRecordCreateDTO dto = new BorrowRecordCreateDTO();
        dto.setUserId(1L);
        dto.setBookId(100L);

        when(userMapper.selectById(1L)).thenReturn(user);
        when(bookMapper.selectById(100L)).thenReturn(book);

        assertThatThrownBy(() -> borrowRecordService.borrowBook(dto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void borrowBook_ShouldThrowException_WhenStockInsufficient() {
        User user = new User().setId(1L).setName("张三").setRole("DEFAULT");
        Book book = new Book().setId(100L).setTitle("测试图书").setStatus(1);
        BorrowRule rule = new BorrowRule()
                .setId(1L).setRole("DEFAULT")
                .setMaxBorrowCount(5).setMaxBorrowDays(30)
                .setMaxRenewCount(1)
                .setOverdueFeePerDay(BigDecimal.valueOf(0.50))
                .setStatus(1);
        BookStock stock = new BookStock()
                .setId(1L).setBookId(100L)
                .setTotalCount(0).setAvailableCount(0)
                .setBorrowedCount(0).setLostCount(0).setDamagedCount(0);

        BorrowRecordCreateDTO dto = new BorrowRecordCreateDTO();
        dto.setUserId(1L);
        dto.setBookId(100L);

        when(userMapper.selectById(1L)).thenReturn(user);
        when(bookMapper.selectById(100L)).thenReturn(book);
        when(borrowRuleService.getRuleByRole("DEFAULT")).thenReturn(rule);
        when(borrowRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(bookStockService.getStockEntityByBookId(100L)).thenReturn(stock);

        assertThatThrownBy(() -> borrowRecordService.borrowBook(dto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void returnBook_ShouldSucceed() {
        BorrowRecord record = new BorrowRecord()
                .setId(1L).setUserId(1L).setBookId(100L)
                .setBorrowTime(LocalDateTime.now().minusDays(5))
                .setDueTime(LocalDateTime.now().plusDays(25))
                .setStatus(BorrowStatus.BORROWED.getCode())
                .setRenewCount(0);

        when(borrowRecordMapper.selectById(1L)).thenReturn(record);
        when(borrowRecordMapper.updateById(any(BorrowRecord.class))).thenReturn(1);

        BorrowRecordVO result = borrowRecordService.returnBook(1L, null);

        assertThat(result.getStatus()).isEqualTo(BorrowStatus.RETURNED.getCode());
        verify(bookStockService).increaseAvailable(100L);
        verify(bookOperationLogService).recordLog(anyLong(), anyLong(), any(), anyString(), anyString(), any());
    }

    @Test
    void returnBook_ShouldThrowException_WhenAlreadyReturned() {
        BorrowRecord record = new BorrowRecord()
                .setId(1L).setUserId(1L).setBookId(100L)
                .setStatus(BorrowStatus.RETURNED.getCode());

        when(borrowRecordMapper.selectById(1L)).thenReturn(record);

        assertThatThrownBy(() -> borrowRecordService.returnBook(1L, null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void renewBook_ShouldSucceed_WhenNotExceedLimit() {
        BorrowRecord record = new BorrowRecord()
                .setId(1L).setUserId(1L).setBookId(100L)
                .setBorrowTime(LocalDateTime.now().minusDays(5))
                .setDueTime(LocalDateTime.now().plusDays(25))
                .setStatus(BorrowStatus.BORROWED.getCode())
                .setRenewCount(0);
        BorrowRule rule = new BorrowRule()
                .setId(1L).setRole("DEFAULT")
                .setMaxBorrowCount(5).setMaxBorrowDays(30)
                .setMaxRenewCount(2)
                .setOverdueFeePerDay(BigDecimal.valueOf(0.50))
                .setStatus(1);

        when(borrowRecordMapper.selectById(1L)).thenReturn(record);
        when(userMapper.selectById(1L)).thenReturn(new User().setId(1L).setRole("DEFAULT"));
        when(borrowRuleService.getRuleByRole("DEFAULT")).thenReturn(rule);
        when(borrowRecordMapper.updateById(any(BorrowRecord.class))).thenReturn(1);

        BorrowRecordVO result = borrowRecordService.renewBook(1L);

        assertThat(result.getRenewCount()).isEqualTo(1);
    }

    @Test
    void renewBook_ShouldThrowException_WhenExceedLimit() {
        BorrowRecord record = new BorrowRecord()
                .setId(1L).setUserId(1L).setBookId(100L)
                .setStatus(BorrowStatus.BORROWED.getCode())
                .setRenewCount(2);
        BorrowRule rule = new BorrowRule()
                .setId(1L).setRole("DEFAULT")
                .setMaxBorrowCount(5).setMaxBorrowDays(30)
                .setMaxRenewCount(2)
                .setOverdueFeePerDay(BigDecimal.valueOf(0.50))
                .setStatus(1);

        when(borrowRecordMapper.selectById(1L)).thenReturn(record);
        when(userMapper.selectById(1L)).thenReturn(new User().setId(1L).setRole("DEFAULT"));
        when(borrowRuleService.getRuleByRole("DEFAULT")).thenReturn(rule);

        assertThatThrownBy(() -> borrowRecordService.renewBook(1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getBorrowRecordById_ShouldReturnRecord() {
        BorrowRecord record = new BorrowRecord()
                .setId(1L).setUserId(1L).setBookId(100L)
                .setStatus(BorrowStatus.BORROWED.getCode())
                .setDueTime(LocalDateTime.now().plusDays(10));
        when(borrowRecordMapper.selectById(1L)).thenReturn(record);

        BorrowRecordVO result = borrowRecordService.getBorrowRecordById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getBorrowRecordById_ShouldThrowException_WhenNotFound() {
        when(borrowRecordMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> borrowRecordService.getBorrowRecordById(999L))
                .isInstanceOf(BusinessException.class);
    }
}
