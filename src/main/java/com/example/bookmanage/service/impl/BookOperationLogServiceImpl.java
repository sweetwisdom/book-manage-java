package com.example.bookmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.common.exception.ErrorCode;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.BookOperationLogQueryDTO;
import com.example.bookmanage.model.entity.Book;
import com.example.bookmanage.model.entity.BookOperationLog;
import com.example.bookmanage.model.entity.User;
import com.example.bookmanage.model.enums.OperationType;
import com.example.bookmanage.model.mapper.BookMapper;
import com.example.bookmanage.model.mapper.BookOperationLogMapper;
import com.example.bookmanage.model.mapper.UserMapper;
import com.example.bookmanage.model.vo.BookOperationLogVO;
import com.example.bookmanage.service.BookOperationLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 操作日志 Service 实现
 */
@Service
public class BookOperationLogServiceImpl extends ServiceImpl<BookOperationLogMapper, BookOperationLog>
        implements BookOperationLogService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResponse<BookOperationLogVO> getLogPage(BookOperationLogQueryDTO queryDTO) {
        LambdaQueryWrapper<BookOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getBookId() != null) {
            wrapper.eq(BookOperationLog::getBookId, queryDTO.getBookId());
        }
        if (queryDTO.getOperatorId() != null) {
            wrapper.eq(BookOperationLog::getOperatorId, queryDTO.getOperatorId());
        }
        if (StringUtils.hasText(queryDTO.getOperationType())) {
            wrapper.eq(BookOperationLog::getOperationType, queryDTO.getOperationType());
        }
        wrapper.orderByDesc(BookOperationLog::getCreateTime);

        Page<BookOperationLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<BookOperationLog> logPage = baseMapper.selectPage(page, wrapper);

        List<BookOperationLogVO> records = logPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        fillBookTitles(records);
        fillOperatorNames(records);

        return new PageResponse<>(
                records,
                logPage.getTotal(),
                logPage.getCurrent(),
                logPage.getSize(),
                logPage.getPages()
        );
    }

    @Override
    public BookOperationLogVO getLogById(Long id) {
        BookOperationLog log = baseMapper.selectById(id);
        if (log == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "操作日志不存在");
        }
        BookOperationLogVO vo = convertToVO(log);
        fillBookTitles(Collections.singletonList(vo));
        fillOperatorNames(Collections.singletonList(vo));
        return vo;
    }

    @Override
    public void recordLog(Long bookId, Long operatorId, OperationType type,
                          String beforeData, String afterData, String remark) {
        BookOperationLog log = new BookOperationLog()
                .setBookId(bookId)
                .setOperatorId(operatorId)
                .setOperationType(type.getCode())
                .setBeforeData(beforeData)
                .setAfterData(afterData)
                .setRemark(remark);
        this.save(log);
    }

    private void fillBookTitles(List<BookOperationLogVO> vos) {
        Set<Long> bookIds = vos.stream()
                .map(BookOperationLogVO::getBookId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (bookIds.isEmpty()) {
            return;
        }

        List<Book> books = bookMapper.selectBatchIds(bookIds);
        Map<Long, String> bookTitleMap = books.stream()
                .collect(Collectors.toMap(Book::getId, Book::getTitle));

        vos.forEach(vo -> {
            if (vo.getBookId() != null) {
                vo.setBookTitle(bookTitleMap.get(vo.getBookId()));
            }
        });
    }

    private void fillOperatorNames(List<BookOperationLogVO> vos) {
        Set<Long> operatorIds = vos.stream()
                .map(BookOperationLogVO::getOperatorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (operatorIds.isEmpty()) {
            return;
        }

        List<User> users = userMapper.selectBatchIds(operatorIds);
        Map<Long, String> userNameMap = users.stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        vos.forEach(vo -> {
            if (vo.getOperatorId() != null) {
                vo.setOperatorName(userNameMap.get(vo.getOperatorId()));
            }
        });
    }

    private BookOperationLogVO convertToVO(BookOperationLog log) {
        BookOperationLogVO vo = new BookOperationLogVO();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }
}
