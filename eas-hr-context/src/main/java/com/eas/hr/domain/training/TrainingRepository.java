package com.eas.hr.domain.training;

import com.eas.common.ddd.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 培训计划资源库接口
 */
public interface TrainingRepository extends Repository<TrainingPlan, TrainingId> {

    /**
     * 根据状态查找培训计划
     *
     * @param status 状态
     * @return 培训计划列表
     */
    List<TrainingPlan> findByStatus(TrainingStatus status);

    /**
     * 根据员工ID查找已报名的培训计划
     *
     * @param employeeId 员工ID
     * @return 培训计划列表
     */
    List<TrainingPlan> findByEnrolledEmployee(String employeeId);

    /**
     * 查找指定日期范围内的培训计划
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 培训计划列表
     */
    List<TrainingPlan> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 查找可报名的培训计划
     *
     * @return 已发布且未满员的培训计划列表
     */
    List<TrainingPlan> findAvailableForEnrollment();
}
