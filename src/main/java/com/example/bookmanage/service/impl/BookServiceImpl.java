package com.example.bookmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.common.exception.ErrorCode;
import com.example.bookmanage.common.response.PageResponse;
import com.example.bookmanage.model.dto.BookDTO;
import com.example.bookmanage.model.dto.BookQueryDTO;
import com.example.bookmanage.model.entity.Book;
import com.example.bookmanage.model.entity.BookCategory;
import com.example.bookmanage.model.enums.BookStatus;
import com.example.bookmanage.model.mapper.BookCategoryMapper;
import com.example.bookmanage.model.mapper.BookMapper;
import com.example.bookmanage.model.vo.BookVO;
import com.example.bookmanage.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 图书Service实现
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Autowired
    private BookCategoryMapper bookCategoryMapper;

    @Override
    public PageResponse<BookVO> getBookPage(BookQueryDTO queryDTO) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getTitle())) {
            wrapper.like(Book::getTitle, queryDTO.getTitle());
        }
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Book::getCategoryId, queryDTO.getCategoryId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Book::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByDesc(Book::getCreateTime)
                .orderByDesc(Book::getId);

        Page<Book> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Book> bookPage = baseMapper.selectPage(page, wrapper);

        List<BookVO> records = bookPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 批量填充分类名称
        fillCategoryNames(records);

        return new PageResponse<>(
                records,
                bookPage.getTotal(),
                bookPage.getCurrent(),
                bookPage.getSize(),
                bookPage.getPages()
        );
    }

    @Override
    public BookVO getBookById(Long id) {
        Book book = baseMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
        }
        BookVO vo = convertToVO(book);
        fillCategoryNames(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional
    public BookVO createBook(BookDTO dto) {
        validateBookStatus(dto.getStatus());

        // ISBN唯一校验
        if (StringUtils.hasText(dto.getIsbn())) {
            checkIsbnDuplicate(dto.getIsbn(), null);
        }

        Book book = new Book();
        BeanUtils.copyProperties(dto, book);
        // 处理日期字符串
        if (StringUtils.hasText(dto.getPublishDate())) {
            try {
                book.setPublishDate(LocalDate.parse(dto.getPublishDate()));
            } catch (DateTimeParseException e) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "出版日期格式错误，正确格式为 yyyy-MM-dd");
            }
        }

        this.save(book);
        BookVO vo = convertToVO(book);
        fillCategoryNames(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional
    public BookVO updateBook(Long id, BookDTO dto) {
        validateBookStatus(dto.getStatus());

        Book existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
        }

        // ISBN唯一校验（排除自身）
        if (StringUtils.hasText(dto.getIsbn())) {
            checkIsbnDuplicate(dto.getIsbn(), id);
        }

        Book book = new Book();
        BeanUtils.copyProperties(dto, book);
        book.setId(id);
        if (StringUtils.hasText(dto.getPublishDate())) {
            try {
                book.setPublishDate(LocalDate.parse(dto.getPublishDate()));
            } catch (DateTimeParseException e) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "出版日期格式错误，正确格式为 yyyy-MM-dd");
            }
        }

        this.updateById(book);
        Book updated = baseMapper.selectById(id);
        BookVO vo = convertToVO(updated);
        fillCategoryNames(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = baseMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
        }
        // @TableLogic 会将此操作转为 UPDATE SET deleted=1
        this.removeById(id);
    }

    @Override
    @Transactional
    public BookVO updateBookStatus(Long id, Integer status) {
        if (!BookStatus.isValid(status)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "状态值只能为0(下架)或1(上架)");
        }
        Book book = baseMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
        }
        Book update = new Book();
        update.setId(id);
        update.setStatus(status);
        this.updateById(update);

        Book updated = baseMapper.selectById(id);
        BookVO vo = convertToVO(updated);
        fillCategoryNames(Collections.singletonList(vo));
        return vo;
    }

    private void validateBookStatus(Integer status) {
        if (!BookStatus.isValid(status)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "状态值只能为0(下架)或1(上架)");
        }
    }

    /**
     * 检查ISBN是否重复
     */
    private void checkIsbnDuplicate(String isbn, Long excludeId) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getIsbn, isbn);
        if (excludeId != null) {
            wrapper.ne(Book::getId, excludeId);
        }
        Long count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.ISBN_DUPLICATE);
        }
    }

    /**
     * 批量填充分类名称
     */
    private void fillCategoryNames(List<BookVO> vos) {
        Set<Long> categoryIds = vos.stream()
                .map(BookVO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (categoryIds.isEmpty()) {
            return;
        }

        List<BookCategory> categories = bookCategoryMapper.selectBatchIds(categoryIds);
        Map<Long, String> categoryNameMap = categories.stream()
                .collect(Collectors.toMap(BookCategory::getId, BookCategory::getName));

        vos.forEach(vo -> {
            if (vo.getCategoryId() != null) {
                vo.setCategoryName(categoryNameMap.get(vo.getCategoryId()));
            }
        });
    }

    /**
     * Entity -> VO 转换
     */
    private BookVO convertToVO(Book book) {
        BookVO vo = new BookVO();
        vo.setId(book.getId());
        vo.setIsbn(book.getIsbn());
        vo.setTitle(book.getTitle());
        vo.setAuthor(book.getAuthor());
        vo.setPublisher(book.getPublisher());
        vo.setPublishDate(book.getPublishDate());
        vo.setCategoryId(book.getCategoryId());
        vo.setDescription(book.getDescription());
        vo.setCoverUrl(book.getCoverUrl());
        vo.setStatus(book.getStatus());
        vo.setCreateTime(book.getCreateTime());
        vo.setUpdateTime(book.getUpdateTime());
        return vo;
    }
}
