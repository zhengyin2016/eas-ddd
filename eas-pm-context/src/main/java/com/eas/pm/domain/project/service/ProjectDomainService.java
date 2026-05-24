package com.eas.pm.domain.project.service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 项目领域服务
 * 处理跨Project聚合的业务规则
 */
public class ProjectDomainService {

    /**
     * 验证项目日期合法性
     */
    public void validateProjectDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("项目开始日期和结束日期不能为空");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("项目结束日期不能早于开始日期");
        }
    }

    /**
     * 检查项目预算合法性
     */
    public void checkProjectBudget(BigDecimal budget) {
        if (budget == null) {
            throw new IllegalArgumentException("项目预算不能为空");
        }
        if (budget.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("项目预算不能为负数");
        }
    }
}
