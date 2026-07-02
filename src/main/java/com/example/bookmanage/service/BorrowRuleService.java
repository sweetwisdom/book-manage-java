package com.example.bookmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.bookmanage.model.dto.BorrowRuleDTO;
import com.example.bookmanage.model.entity.BorrowRule;
import com.example.bookmanage.model.vo.BorrowRuleVO;

import java.util.List;

/**
 * 借阅规则 Service 接口
 */
public interface BorrowRuleService extends IService<BorrowRule> {

    /**
     * 查询规则列表
     */
    List<BorrowRuleVO> getRuleList(String role);

    /**
     * 根据ID查询规则
     */
    BorrowRuleVO getRuleById(Long id);

    /**
     * 根据角色查询规则
     */
    BorrowRule getRuleByRole(String role);

    /**
     * 创建规则
     */
    BorrowRuleVO createRule(BorrowRuleDTO dto);

    /**
     * 更新规则
     */
    BorrowRuleVO updateRule(Long id, BorrowRuleDTO dto);

    /**
     * 删除规则
     */
    void deleteRule(Long id);

    /**
     * 启用/禁用规则
     */
    BorrowRuleVO updateRuleStatus(Long id, Integer status);
}
