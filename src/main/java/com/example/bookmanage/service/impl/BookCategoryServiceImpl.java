package com.example.bookmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.common.exception.ErrorCode;
import com.example.bookmanage.model.dto.BookCategoryDTO;
import com.example.bookmanage.model.entity.Book;
import com.example.bookmanage.model.entity.BookCategory;
import com.example.bookmanage.model.enums.EnableStatus;
import com.example.bookmanage.model.mapper.BookCategoryMapper;
import com.example.bookmanage.model.mapper.BookMapper;
import com.example.bookmanage.model.vo.BookCategoryVO;
import com.example.bookmanage.service.BookCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 图书分类Service实现
 */
@Service
public class BookCategoryServiceImpl extends ServiceImpl<BookCategoryMapper, BookCategory> implements BookCategoryService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public List<BookCategoryVO> getCategories(Long parentId, Boolean tree) {
        if (Boolean.TRUE.equals(tree)) {
            // 查询全量数据用于构建树
            List<BookCategory> all = baseMapper.selectList(
                    new LambdaQueryWrapper<BookCategory>()
                            .orderByAsc(BookCategory::getSortOrder)
                            .orderByAsc(BookCategory::getId)
            );
            Long rootParentId = parentId != null ? parentId : 0L;
            return buildTree(all, rootParentId);
        }

        // tree=false: 按 parentId 过滤，平铺返回
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        if (parentId != null) {
            wrapper.eq(BookCategory::getParentId, parentId);
        }
        wrapper.orderByAsc(BookCategory::getSortOrder)
                .orderByAsc(BookCategory::getId);

        return baseMapper.selectList(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public BookCategoryVO getCategoryById(Long id) {
        BookCategory category = baseMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return convertToVO(category);
    }

    @Override
    @Transactional
    public BookCategoryVO createCategory(BookCategoryDTO dto) {
        validateCategoryStatus(dto.getStatus());

        // 检查同级分类下名称是否重复
        Long parentId = dto.getParentId() != null ? dto.getParentId() : 0L;
        checkNameDuplicate(parentId, dto.getName(), null);

        BookCategory category = new BookCategory();
        BeanUtils.copyProperties(dto, category);
        if (dto.getParentId() == null) {
            category.setParentId(0L);
        }
        if (dto.getSortOrder() == null) {
            category.setSortOrder(0);
        }

        this.save(category);
        return convertToVO(category);
    }

    @Override
    @Transactional
    public BookCategoryVO updateCategory(Long id, BookCategoryDTO dto) {
        validateCategoryStatus(dto.getStatus());

        BookCategory existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        // 检查同级分类下名称是否重复（排除自身）
        Long parentId = dto.getParentId() != null ? dto.getParentId() : 0L;
        checkNameDuplicate(parentId, dto.getName(), id);

        BookCategory category = new BookCategory();
        BeanUtils.copyProperties(dto, category);
        category.setId(id);
        if (dto.getParentId() == null) {
            category.setParentId(0L);
        }

        this.updateById(category);
        return convertToVO(this.getById(id));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        BookCategory category = baseMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        // 检查是否有子分类
        Long childCount = baseMapper.selectCount(
                new LambdaQueryWrapper<BookCategory>()
                        .eq(BookCategory::getParentId, id)
        );
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_HAS_CHILDREN);
        }

        // 检查是否有关联图书
        Long bookCount = bookMapper.selectCount(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getCategoryId, id)
        );
        if (bookCount > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_HAS_BOOKS);
        }

        baseMapper.deleteById(id);
    }

    /**
     * 检查同级分类下名称是否重复
     */
    private void checkNameDuplicate(Long parentId, String name, Long excludeId) {
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCategory::getParentId, parentId)
                .eq(BookCategory::getName, name);
        if (excludeId != null) {
            wrapper.ne(BookCategory::getId, excludeId);
        }
        Long count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_DUPLICATE);
        }
    }

    private void validateCategoryStatus(Integer status) {
        if (!EnableStatus.isValid(status)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "状态值只能为0(禁用)或1(启用)");
        }
    }

    /**
     * 递归构建分类树
     */
    private List<BookCategoryVO> buildTree(List<BookCategory> all, Long parentId) {
        return all.stream()
                .filter(c -> Objects.equals(c.getParentId(), parentId))
                .map(c -> {
                    BookCategoryVO vo = convertToVO(c);
                    List<BookCategoryVO> children = buildTree(all, c.getId());
                    vo.setChildren(children.isEmpty() ? null : children);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * Entity -> VO 转换
     */
    private BookCategoryVO convertToVO(BookCategory category) {
        BookCategoryVO vo = new BookCategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
}
