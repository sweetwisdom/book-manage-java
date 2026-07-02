package com.example.bookmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.common.exception.ErrorCode;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.BorrowRecordCreateDTO;
import com.example.bookmanage.model.dto.BorrowRecordQueryDTO;
import com.example.bookmanage.model.entity.Book;
import com.example.bookmanage.model.entity.BookStock;
import com.example.bookmanage.model.entity.BorrowRecord;
import com.example.bookmanage.model.entity.BorrowRule;
import com.example.bookmanage.model.entity.User;
import com.example.bookmanage.model.enums.BookStatus;
import com.example.bookmanage.model.enums.BorrowStatus;
import com.example.bookmanage.model.enums.OperationType;
import com.example.bookmanage.model.mapper.BookMapper;
import com.example.bookmanage.model.mapper.BorrowRecordMapper;
import com.example.bookmanage.model.mapper.UserMapper;
import com.example.bookmanage.model.vo.BorrowRecordVO;
import com.example.bookmanage.service.BookOperationLogService;
import com.example.bookmanage.service.BookStockService;
import com.example.bookmanage.service.BorrowRecordService;
import com.example.bookmanage.service.BorrowRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 借阅记录 Service 实现
 */
@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord>
        implements BorrowRecordService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookStockService bookStockService;

    @Autowired
    private BorrowRuleService borrowRuleService;

    @Autowired
    private BookOperationLogService bookOperationLogService;

    @Override
    public PageResponse<BorrowRecordVO> getBorrowRecordPage(BorrowRecordQueryDTO queryDTO) {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getUserId() != null) {
            wrapper.eq(BorrowRecord::getUserId, queryDTO.getUserId());
        }
        if (queryDTO.getBookId() != null) {
            wrapper.eq(BorrowRecord::getBookId, queryDTO.getBookId());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(BorrowRecord::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByDesc(BorrowRecord::getCreateTime);

        Page<BorrowRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<BorrowRecord> recordPage = baseMapper.selectPage(page, wrapper);

        List<BorrowRecordVO> records = recordPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        fillUserNames(records);
        fillBookTitles(records);

        return new PageResponse<>(
                records,
                recordPage.getTotal(),
                recordPage.getCurrent(),
                recordPage.getSize(),
                recordPage.getPages()
        );
    }

    @Override
    public BorrowRecordVO getBorrowRecordById(Long id) {
        BorrowRecord record = baseMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.BORROW_RECORD_NOT_FOUND);
        }
        BorrowRecordVO vo = convertToVO(record);
        fillUserNames(Collections.singletonList(vo));
        fillBookTitles(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional
    public BorrowRecordVO borrowBook(BorrowRecordCreateDTO dto) {
        // 1. 校验用户存在
        User user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 校验图书存在且上架
        Book book = bookMapper.selectById(dto.getBookId());
        if (book == null) {
            throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
        }
        if (!Integer.valueOf(BookStatus.ON_SHELF.getCode()).equals(book.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "图书已下架，无法借阅");
        }

        // 3. 获取用户角色对应的借阅规则
        String role = user.getRole() != null ? user.getRole() : "DEFAULT";
        BorrowRule rule = borrowRuleService.getRuleByRole(role);
        if (rule == null) {
            throw new BusinessException(ErrorCode.BORROW_RULE_NOT_FOUND);
        }
        if (!rule.getStatus().equals(1)) {
            throw new BusinessException(ErrorCode.BORROW_RULE_DISABLED);
        }

        // 4. 校验借阅上限
        long currentBorrowedCount = countUserBorrowedRecords(dto.getUserId());
        if (currentBorrowedCount >= rule.getMaxBorrowCount()) {
            throw new BusinessException(ErrorCode.BORROW_LIMIT_EXCEEDED);
        }

        // 5. 校验用户是否有逾期记录
        if (hasOverdueRecords(dto.getUserId())) {
            throw new BusinessException(ErrorCode.USER_HAS_OVERDUE);
        }

        // 6. 校验库存
        BookStock stock = bookStockService.getStockEntityByBookId(dto.getBookId());
        if (stock == null || stock.getAvailableCount() <= 0) {
            throw new BusinessException(ErrorCode.BOOK_NOT_AVAILABLE);
        }

        // 7. 在事务中执行借阅操作
        bookStockService.decreaseAvailable(dto.getBookId());

        BorrowRecord record = new BorrowRecord()
                .setUserId(dto.getUserId())
                .setBookId(dto.getBookId())
                .setBorrowTime(LocalDateTime.now())
                .setDueTime(LocalDateTime.now().plusDays(rule.getMaxBorrowDays()))
                .setStatus(BorrowStatus.BORROWED.getCode())
                .setRenewCount(0)
                .setRemark(dto.getRemark());
        this.save(record);

        bookOperationLogService.recordLog(
                dto.getBookId(), dto.getUserId(), OperationType.BORROW_BOOK,
                null, "借阅记录ID: " + record.getId(), dto.getRemark());

        BorrowRecordVO vo = convertToVO(record);
        vo.setUserName(user.getName());
        vo.setBookTitle(book.getTitle());
        return vo;
    }

    @Override
    @Transactional
    public BorrowRecordVO returnBook(Long id, String remark) {
        BorrowRecord record = baseMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.BORROW_RECORD_NOT_FOUND);
        }
        if (BorrowStatus.RETURNED.getCode().equals(record.getStatus())
                || BorrowStatus.LOST.getCode().equals(record.getStatus())) {
            throw new BusinessException(ErrorCode.ALREADY_RETURNED);
        }

        bookStockService.increaseAvailable(record.getBookId());

        record.setStatus(BorrowStatus.RETURNED.getCode());
        record.setReturnTime(LocalDateTime.now());
        if (StringUtils.hasText(remark)) {
            record.setRemark(remark);
        }
        this.updateById(record);

        bookOperationLogService.recordLog(
                record.getBookId(), record.getUserId(), OperationType.RETURN_BOOK,
                "借阅记录ID: " + id, "已归还", remark);

        BorrowRecordVO vo = convertToVO(record);
        fillUserNames(Collections.singletonList(vo));
        fillBookTitles(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional
    public BorrowRecordVO renewBook(Long id) {
        BorrowRecord record = baseMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.BORROW_RECORD_NOT_FOUND);
        }
        if (!BorrowStatus.BORROWED.getCode().equals(record.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只有借阅中的记录才能续借");
        }

        String role = "DEFAULT";
        BorrowRule rule = borrowRuleService.getRuleByRole(role);
        if (rule == null) {
            throw new BusinessException(ErrorCode.BORROW_RULE_NOT_FOUND);
        }

        if (record.getRenewCount() >= rule.getMaxRenewCount()) {
            throw new BusinessException(ErrorCode.RENEW_LIMIT_EXCEEDED);
        }

        record.setRenewCount(record.getRenewCount() + 1);
        record.setDueTime(record.getDueTime().plusDays(rule.getMaxBorrowDays()));
        this.updateById(record);

        bookOperationLogService.recordLog(
                record.getBookId(), record.getUserId(), OperationType.BORROW_BOOK,
                "续借前次数: " + (record.getRenewCount() - 1), "续借后次数: " + record.getRenewCount(),
                "续借");

        BorrowRecordVO vo = convertToVO(record);
        fillUserNames(Collections.singletonList(vo));
        fillBookTitles(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional
    public BorrowRecordVO markLost(Long id, String remark) {
        BorrowRecord record = baseMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.BORROW_RECORD_NOT_FOUND);
        }
        if (BorrowStatus.RETURNED.getCode().equals(record.getStatus())
                || BorrowStatus.LOST.getCode().equals(record.getStatus())) {
            throw new BusinessException(ErrorCode.ALREADY_RETURNED);
        }

        bookStockService.increaseLost(record.getBookId());

        record.setStatus(BorrowStatus.LOST.getCode());
        if (StringUtils.hasText(remark)) {
            record.setRemark(remark);
        }
        this.updateById(record);

        bookOperationLogService.recordLog(
                record.getBookId(), record.getUserId(), OperationType.MARK_LOST,
                "借阅记录ID: " + id, "标记丢失", remark);

        BorrowRecordVO vo = convertToVO(record);
        fillUserNames(Collections.singletonList(vo));
        fillBookTitles(Collections.singletonList(vo));
        return vo;
    }

    /**
     * 统计用户当前借阅中的记录数
     */
    private long countUserBorrowedRecords(Long userId) {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getStatus, BorrowStatus.BORROWED.getCode());
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 检查用户是否有逾期记录
     */
    private boolean hasOverdueRecords(Long userId) {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getStatus, BorrowStatus.BORROWED.getCode())
                .lt(BorrowRecord::getDueTime, LocalDateTime.now());
        return baseMapper.selectCount(wrapper) > 0;
    }

    private void fillUserNames(List<BorrowRecordVO> vos) {
        Set<Long> userIds = vos.stream()
                .map(BorrowRecordVO::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (userIds.isEmpty()) {
            return;
        }

        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, String> userNameMap = users.stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        vos.forEach(vo -> {
            if (vo.getUserId() != null) {
                vo.setUserName(userNameMap.get(vo.getUserId()));
            }
        });
    }

    private void fillBookTitles(List<BorrowRecordVO> vos) {
        Set<Long> bookIds = vos.stream()
                .map(BorrowRecordVO::getBookId)
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

    private BorrowRecordVO convertToVO(BorrowRecord record) {
        BorrowRecordVO vo = new BorrowRecordVO();
        BeanUtils.copyProperties(record, vo);
        // 动态计算是否逾期
        if (BorrowStatus.BORROWED.getCode().equals(record.getStatus())
                && record.getDueTime() != null
                && record.getDueTime().isBefore(LocalDateTime.now())) {
            vo.setIsOverdue(true);
        } else {
            vo.setIsOverdue(false);
        }
        return vo;
    }
}
