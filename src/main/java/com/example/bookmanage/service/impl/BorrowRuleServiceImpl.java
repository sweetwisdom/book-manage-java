package com.example.bookmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.common.exception.ErrorCode;
import com.example.bookmanage.model.dto.BorrowRuleDTO;
import com.example.bookmanage.model.entity.BorrowRule;
import com.example.bookmanage.model.enums.EnableStatus;
import com.example.bookmanage.model.mapper.BorrowRuleMapper;
import com.example.bookmanage.model.vo.BorrowRuleVO;
import com.example.bookmanage.service.BorrowRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 借阅规则 Service 实现
 */
@Service
public class BorrowRuleServiceImpl extends ServiceImpl<BorrowRuleMapper, BorrowRule> implements BorrowRuleService {

    @Override
    public List<BorrowRuleVO> getRuleList(String role) {
        LambdaQueryWrapper<BorrowRule> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(role)) {
            wrapper.eq(BorrowRule::getRole, role);
        }
        wrapper.orderByDesc(BorrowRule::getCreateTime);
        List<BorrowRule> rules = baseMapper.selectList(wrapper);
        return rules.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public BorrowRuleVO getRuleById(Long id) {
        BorrowRule rule = baseMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException(ErrorCode.BORROW_RULE_NOT_FOUND);
        }
        return convertToVO(rule);
    }

    @Override
    public BorrowRule getRuleByRole(String role) {
        LambdaQueryWrapper<BorrowRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRule::getRole, role);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public BorrowRuleVO createRule(BorrowRuleDTO dto) {
        // role 唯一校验
        LambdaQueryWrapper<BorrowRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRule::getRole, dto.getRole());
        Long count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.ROLE_DUPLICATE);
        }

        BorrowRule rule = new BorrowRule();
        BeanUtils.copyProperties(dto, rule);
        this.save(rule);
        return convertToVO(rule);
    }

    @Override
    @Transactional
    public BorrowRuleVO updateRule(Long id, BorrowRuleDTO dto) {
        BorrowRule existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.BORROW_RULE_NOT_FOUND);
        }

        // role 唯一校验（排除自身）
        if (!existing.getRole().equals(dto.getRole())) {
            LambdaQueryWrapper<BorrowRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BorrowRule::getRole, dto.getRole());
            Long count = baseMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.ROLE_DUPLICATE);
            }
        }

        BorrowRule rule = new BorrowRule();
        BeanUtils.copyProperties(dto, rule);
        rule.setId(id);
        this.updateById(rule);

        BorrowRule updated = baseMapper.selectById(id);
        return convertToVO(updated);
    }

    @Override
    @Transactional
    public void deleteRule(Long id) {
        BorrowRule rule = baseMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException(ErrorCode.BORROW_RULE_NOT_FOUND);
        }
        this.removeById(id);
    }

    @Override
    @Transactional
    public BorrowRuleVO updateRuleStatus(Long id, Integer status) {
        if (!EnableStatus.isValid(status)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "状态值只能为0(禁用)或1(启用)");
        }
        BorrowRule rule = baseMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException(ErrorCode.BORROW_RULE_NOT_FOUND);
        }
        BorrowRule update = new BorrowRule();
        update.setId(id);
        update.setStatus(status);
        this.updateById(update);

        BorrowRule updated = baseMapper.selectById(id);
        return convertToVO(updated);
    }

    private BorrowRuleVO convertToVO(BorrowRule rule) {
        BorrowRuleVO vo = new BorrowRuleVO();
        BeanUtils.copyProperties(rule, vo);
        return vo;
    }
}
