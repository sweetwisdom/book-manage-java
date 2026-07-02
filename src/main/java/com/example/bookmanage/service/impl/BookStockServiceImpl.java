package com.example.bookmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.common.exception.ErrorCode;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.BookStockAdjustDTO;
import com.example.bookmanage.model.entity.Book;
import com.example.bookmanage.model.entity.BookStock;
import com.example.bookmanage.model.mapper.BookMapper;
import com.example.bookmanage.model.mapper.BookStockMapper;
import com.example.bookmanage.model.vo.BookStockVO;
import com.example.bookmanage.service.BookStockService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 图书库存 Service 实现
 */
@Service
public class BookStockServiceImpl extends ServiceImpl<BookStockMapper, BookStock> implements BookStockService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public PageResponse<BookStockVO> getStockPage(Long pageNum, Long pageSize, Long bookId) {
        LambdaQueryWrapper<BookStock> wrapper = new LambdaQueryWrapper<>();
        if (bookId != null) {
            wrapper.eq(BookStock::getBookId, bookId);
        }
        wrapper.orderByDesc(BookStock::getUpdateTime);

        Page<BookStock> page = new Page<>(pageNum, pageSize);
        Page<BookStock> stockPage = baseMapper.selectPage(page, wrapper);

        List<BookStockVO> records = stockPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        fillBookTitles(records);

        return new PageResponse<>(
                records,
                stockPage.getTotal(),
                stockPage.getCurrent(),
                stockPage.getSize(),
                stockPage.getPages()
        );
    }

    @Override
    public BookStockVO getStockByBookId(Long bookId) {
        LambdaQueryWrapper<BookStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookStock::getBookId, bookId);
        BookStock stock = baseMapper.selectOne(wrapper);
        if (stock == null) {
            throw new BusinessException(ErrorCode.STOCK_NOT_FOUND);
        }
        BookStockVO vo = convertToVO(stock);
        fillBookTitles(Collections.singletonList(vo));
        return vo;
    }

    @Override
    public BookStockVO getStockById(Long id) {
        BookStock stock = baseMapper.selectById(id);
        if (stock == null) {
            throw new BusinessException(ErrorCode.STOCK_NOT_FOUND);
        }
        BookStockVO vo = convertToVO(stock);
        fillBookTitles(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional
    public BookStockVO adjustStock(Long bookId, BookStockAdjustDTO dto) {
        BookStock stock = getStockByBookIdInternal(bookId);

        int adjustCount = dto.getAdjustCount();
        String adjustType = dto.getAdjustType().toUpperCase();
        String adjustField = dto.getAdjustField().toUpperCase();

        if (!"INCREASE".equals(adjustType) && !"DECREASE".equals(adjustType)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "调整类型只能为 INCREASE 或 DECREASE");
        }

        int change = "INCREASE".equals(adjustType) ? adjustCount : -adjustCount;

        switch (adjustField) {
            case "TOTAL":
                int newTotal = stock.getTotalCount() + change;
                if (newTotal < 0) {
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "总库存不足，无法减少");
                }
                stock.setTotalCount(newTotal);
                stock.setAvailableCount(stock.getAvailableCount() + change);
                break;
            case "AVAILABLE":
                int newAvailable = stock.getAvailableCount() + change;
                if (newAvailable < 0) {
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "可借数量不足，无法减少");
                }
                stock.setAvailableCount(newAvailable);
                stock.setTotalCount(stock.getTotalCount() + change);
                break;
            case "LOST":
                int newLost = stock.getLostCount() + change;
                if (newLost < 0) {
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "丢失数量不能为负数");
                }
                stock.setLostCount(newLost);
                break;
            case "DAMAGED":
                int newDamaged = stock.getDamagedCount() + change;
                if (newDamaged < 0) {
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "损坏数量不能为负数");
                }
                stock.setDamagedCount(newDamaged);
                break;
            default:
                throw new BusinessException(ErrorCode.PARAM_ERROR,
                        "调整字段只能为 TOTAL/AVAILABLE/LOST/DAMAGED");
        }

        this.updateById(stock);

        BookStock updated = baseMapper.selectById(stock.getId());
        BookStockVO vo = convertToVO(updated);
        fillBookTitles(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional
    public void initStock(Long bookId, Integer totalCount) {
        BookStock stock = new BookStock()
                .setBookId(bookId)
                .setTotalCount(totalCount)
                .setAvailableCount(totalCount)
                .setBorrowedCount(0)
                .setLostCount(0)
                .setDamagedCount(0);
        this.save(stock);
    }

    @Override
    @Transactional
    public void decreaseAvailable(Long bookId) {
        BookStock stock = getStockByBookIdInternal(bookId);
        if (stock.getAvailableCount() <= 0) {
            throw new BusinessException(ErrorCode.BOOK_NOT_AVAILABLE);
        }
        stock.setAvailableCount(stock.getAvailableCount() - 1);
        stock.setBorrowedCount(stock.getBorrowedCount() + 1);
        this.updateById(stock);
    }

    @Override
    @Transactional
    public void increaseAvailable(Long bookId) {
        BookStock stock = getStockByBookIdInternal(bookId);
        stock.setAvailableCount(stock.getAvailableCount() + 1);
        stock.setBorrowedCount(stock.getBorrowedCount() - 1);
        this.updateById(stock);
    }

    @Override
    @Transactional
    public void increaseLost(Long bookId) {
        BookStock stock = getStockByBookIdInternal(bookId);
        stock.setBorrowedCount(stock.getBorrowedCount() - 1);
        stock.setLostCount(stock.getLostCount() + 1);
        this.updateById(stock);
    }

    @Override
    public BookStock getStockEntityByBookId(Long bookId) {
        return getStockByBookIdInternal(bookId);
    }

    /**
     * 内部方法：根据 bookId 查询库存（不填充关联名称）
     */
    private BookStock getStockByBookIdInternal(Long bookId) {
        LambdaQueryWrapper<BookStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookStock::getBookId, bookId);
        BookStock stock = baseMapper.selectOne(wrapper);
        if (stock == null) {
            throw new BusinessException(ErrorCode.STOCK_NOT_FOUND);
        }
        return stock;
    }

    private void fillBookTitles(List<BookStockVO> vos) {
        Set<Long> bookIds = vos.stream()
                .map(BookStockVO::getBookId)
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

    private BookStockVO convertToVO(BookStock stock) {
        BookStockVO vo = new BookStockVO();
        BeanUtils.copyProperties(stock, vo);
        return vo;
    }
}
