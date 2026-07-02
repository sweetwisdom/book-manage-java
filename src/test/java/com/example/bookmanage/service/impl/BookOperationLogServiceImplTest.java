package com.example.bookmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bookmanage.model.dto.BookOperationLogQueryDTO;
import com.example.bookmanage.model.entity.BookOperationLog;
import com.example.bookmanage.model.enums.OperationType;
import com.example.bookmanage.model.mapper.BookMapper;
import com.example.bookmanage.model.mapper.BookOperationLogMapper;
import com.example.bookmanage.model.mapper.UserMapper;
import com.example.bookmanage.model.vo.BookOperationLogVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookOperationLogServiceImplTest {

    @Mock
    private BookOperationLogMapper bookOperationLogMapper;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private UserMapper userMapper;

    private BookOperationLogServiceImpl bookOperationLogService;

    @BeforeEach
    void setUp() {
        bookOperationLogService = new BookOperationLogServiceImpl();
        ReflectionTestUtils.setField(bookOperationLogService, "baseMapper", bookOperationLogMapper);
        ReflectionTestUtils.setField(bookOperationLogService, "bookMapper", bookMapper);
        ReflectionTestUtils.setField(bookOperationLogService, "userMapper", userMapper);
    }

    @Test
    void getLogPage_ShouldReturnPageResult() {
        BookOperationLog log = new BookOperationLog()
                .setId(1L).setBookId(100L).setOperatorId(10L)
                .setOperationType("BORROW_BOOK")
                .setRemark("测试借阅")
                .setCreateTime(LocalDateTime.now());

        Page<BookOperationLog> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(log));
        page.setTotal(1);

        when(bookOperationLogMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        BookOperationLogQueryDTO queryDTO = new BookOperationLogQueryDTO();
        queryDTO.setPageNum(1L);
        queryDTO.setPageSize(10L);

        var result = bookOperationLogService.getLogPage(queryDTO);

        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRecords()).hasSize(1);
    }

    @Test
    void getLogPage_ShouldFilterByOperationType() {
        Page<BookOperationLog> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        page.setTotal(0);

        when(bookOperationLogMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        BookOperationLogQueryDTO queryDTO = new BookOperationLogQueryDTO();
        queryDTO.setPageNum(1L);
        queryDTO.setPageSize(10L);
        queryDTO.setOperationType("BORROW_BOOK");

        var result = bookOperationLogService.getLogPage(queryDTO);

        assertThat(result.getTotal()).isEqualTo(0);
    }

    @Test
    void recordLog_ShouldSaveLog() {
        when(bookOperationLogMapper.insert(any(BookOperationLog.class))).thenReturn(1);

        bookOperationLogService.recordLog(100L, 10L, OperationType.BORROW_BOOK,
                null, "借阅记录ID: 1", "备注");

        verify(bookOperationLogMapper).insert(any(BookOperationLog.class));
    }

    @Test
    void getLogById_ShouldReturnLog_WhenExists() {
        BookOperationLog log = new BookOperationLog()
                .setId(1L).setBookId(100L).setOperatorId(10L)
                .setOperationType("BORROW_BOOK")
                .setCreateTime(LocalDateTime.now());
        when(bookOperationLogMapper.selectById(1L)).thenReturn(log);

        BookOperationLogVO result = bookOperationLogService.getLogById(1L);

        assertThat(result.getBookId()).isEqualTo(100L);
        assertThat(result.getOperationType()).isEqualTo("BORROW_BOOK");
    }
}
