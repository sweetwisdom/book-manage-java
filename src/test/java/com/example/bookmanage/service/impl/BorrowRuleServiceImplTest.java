package com.example.bookmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.model.dto.BorrowRuleDTO;
import com.example.bookmanage.model.entity.BorrowRule;
import com.example.bookmanage.model.mapper.BorrowRuleMapper;
import com.example.bookmanage.model.vo.BorrowRuleVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowRuleServiceImplTest {

    @Mock
    private BorrowRuleMapper borrowRuleMapper;

    private BorrowRuleServiceImpl borrowRuleService;

    @BeforeEach
    void setUp() {
        borrowRuleService = new BorrowRuleServiceImpl();
        ReflectionTestUtils.setField(borrowRuleService, "baseMapper", borrowRuleMapper);
    }

    @Test
    void getRuleById_ShouldReturnRule_WhenExists() {
        BorrowRule rule = new BorrowRule()
                .setId(1L).setRole("DEFAULT")
                .setMaxBorrowCount(5).setMaxBorrowDays(30)
                .setMaxRenewCount(1).setOverdueFeePerDay(BigDecimal.valueOf(0.50))
                .setStatus(1);
        when(borrowRuleMapper.selectById(1L)).thenReturn(rule);

        BorrowRuleVO result = borrowRuleService.getRuleById(1L);

        assertThat(result.getRole()).isEqualTo("DEFAULT");
        assertThat(result.getMaxBorrowCount()).isEqualTo(5);
    }

    @Test
    void getRuleById_ShouldThrowException_WhenNotFound() {
        when(borrowRuleMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> borrowRuleService.getRuleById(999L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void createRule_ShouldSucceed_WhenRoleNotDuplicate() {
        BorrowRuleDTO dto = new BorrowRuleDTO();
        dto.setRole("VIP");
        dto.setMaxBorrowCount(10);
        dto.setMaxBorrowDays(60);
        dto.setMaxRenewCount(2);
        dto.setOverdueFeePerDay(BigDecimal.ZERO);
        dto.setStatus(1);

        when(borrowRuleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(borrowRuleMapper.insert(any(BorrowRule.class))).thenReturn(1);

        BorrowRuleVO result = borrowRuleService.createRule(dto);

        assertThat(result.getRole()).isEqualTo("VIP");
    }

    @Test
    void createRule_ShouldThrowException_WhenRoleDuplicate() {
        BorrowRuleDTO dto = new BorrowRuleDTO();
        dto.setRole("DEFAULT");
        dto.setMaxBorrowCount(5);
        dto.setMaxBorrowDays(30);
        dto.setMaxRenewCount(1);
        dto.setOverdueFeePerDay(BigDecimal.valueOf(0.50));
        dto.setStatus(1);

        when(borrowRuleMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        assertThatThrownBy(() -> borrowRuleService.createRule(dto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updateRule_ShouldSucceed_WhenExists() {
        BorrowRule existing = new BorrowRule()
                .setId(1L).setRole("DEFAULT")
                .setMaxBorrowCount(5).setMaxBorrowDays(30)
                .setMaxRenewCount(1).setOverdueFeePerDay(BigDecimal.valueOf(0.50))
                .setStatus(1);

        BorrowRuleDTO dto = new BorrowRuleDTO();
        dto.setRole("DEFAULT");
        dto.setMaxBorrowCount(8);
        dto.setMaxBorrowDays(45);
        dto.setMaxRenewCount(2);
        dto.setOverdueFeePerDay(BigDecimal.valueOf(1.00));
        dto.setStatus(1);

        when(borrowRuleMapper.selectById(1L)).thenReturn(existing);
        when(borrowRuleMapper.updateById(any(BorrowRule.class))).thenReturn(1);

        BorrowRule updated = new BorrowRule()
                .setId(1L).setRole("DEFAULT")
                .setMaxBorrowCount(8).setMaxBorrowDays(45)
                .setMaxRenewCount(2).setOverdueFeePerDay(BigDecimal.valueOf(1.00))
                .setStatus(1);
        when(borrowRuleMapper.selectById(1L)).thenReturn(updated);

        BorrowRuleVO result = borrowRuleService.updateRule(1L, dto);
        assertThat(result.getMaxBorrowCount()).isEqualTo(8);
    }

    @Test
    void updateRule_ShouldThrowException_WhenNotFound() {
        BorrowRuleDTO dto = new BorrowRuleDTO();
        dto.setRole("DEFAULT");
        dto.setMaxBorrowCount(5);
        dto.setMaxBorrowDays(30);
        dto.setMaxRenewCount(1);
        dto.setOverdueFeePerDay(BigDecimal.valueOf(0.50));
        dto.setStatus(1);
        when(borrowRuleMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> borrowRuleService.updateRule(999L, dto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void deleteRule_ShouldSucceed_WhenExists() {
        BorrowRule rule = new BorrowRule().setId(1L);
        when(borrowRuleMapper.selectById(1L)).thenReturn(rule);
        when(borrowRuleMapper.deleteById(1L)).thenReturn(1);

        borrowRuleService.deleteRule(1L);

        verify(borrowRuleMapper).deleteById(1L);
    }

    @Test
    void deleteRule_ShouldThrowException_WhenNotFound() {
        when(borrowRuleMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> borrowRuleService.deleteRule(999L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updateRuleStatus_ShouldSucceed() {
        BorrowRule rule = new BorrowRule().setId(1L).setStatus(1);
        when(borrowRuleMapper.selectById(1L)).thenReturn(rule);
        when(borrowRuleMapper.updateById(any(BorrowRule.class))).thenReturn(1);

        BorrowRule updated = new BorrowRule().setId(1L).setStatus(0);
        when(borrowRuleMapper.selectById(1L)).thenReturn(updated);

        BorrowRuleVO result = borrowRuleService.updateRuleStatus(1L, 0);
        assertThat(result.getStatus()).isEqualTo(0);
    }
}
