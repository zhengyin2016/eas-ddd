package com.eas.hr.domain.recruitment;

import com.eas.common.ddd.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 招聘需求资源库接口
 */
public interface RecruitmentRepository extends Repository<RecruitmentRequirement, RecruitmentId> {

    /**
     * 根据状态查找招聘需求
     *
     * @param status 状态
     * @return 招聘需求列表
     */
    List<RecruitmentRequirement> findByStatus(RecruitmentStatus status);

    /**
     * 根据部门ID查找招聘需求
     *
     * @param departmentId 部门ID
     * @return 招聘需求列表
     */
    List<RecruitmentRequirement> findByDepartmentId(String departmentId);

    /**
     * 查找待审批的招聘需求
     *
     * @return 待审批的招聘需求列表
     */
    List<RecruitmentRequirement> findPendingApproval();

    /**
     * 查找已完成审批但未招满的招聘需求
     *
     * @return 招聘需求列表
     */
    List<RecruitmentRequirement> findApprovedAndOpen();
}
