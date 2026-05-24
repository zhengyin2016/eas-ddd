package com.eas.hr.northbound.appservice;

import com.eas.hr.domain.employee.DepartmentId;
import com.eas.hr.domain.employee.PositionId;
import com.eas.hr.domain.recruitment.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 招聘应用服务
 */
@Service
public class RecruitmentAppService {

    private final RecruitmentRepository recruitmentRepository;

    public RecruitmentAppService(RecruitmentRepository recruitmentRepository) {
        this.recruitmentRepository = recruitmentRepository;
    }

    /**
     * 提交招聘需求
     *
     * @param title        标题
     * @param departmentId 部门ID
     * @param positionId   岗位ID
     * @param count        招聘人数
     * @param description  岗位描述
     * @param requirements 任职要求
     * @param createdBy    创建人
     * @return 招聘需求响应
     */
    @Transactional
    public RecruitmentRequirement submitRequirement(String title, String departmentId, String positionId,
                                                   int count, String description, String requirements,
                                                   String createdBy) {
        RecruitmentRequirement requirement = RecruitmentRequirement.create(
                title,
                DepartmentId.of(departmentId),
                PositionId.of(positionId),
                count,
                description,
                requirements,
                createdBy
        );

        requirement.submit();
        requirement = recruitmentRepository.save(requirement);

        return requirement;
    }

    /**
     * 审批通过招聘需求
     *
     * @param requirementId 招聘需求ID
     * @param approver      审批人
     */
    @Transactional
    public void approveRequirement(String requirementId, String approver) {
        RecruitmentRequirement requirement = getRequirementEntity(requirementId);
        requirement.approve(approver);
        recruitmentRepository.save(requirement);
    }

    /**
     * 审批拒绝招聘需求
     *
     * @param requirementId 招聘需求ID
     * @param approver      审批人
     * @param reason        拒绝原因
     */
    @Transactional
    public void rejectRequirement(String requirementId, String approver, String reason) {
        RecruitmentRequirement requirement = getRequirementEntity(requirementId);
        requirement.reject(approver, reason);
        recruitmentRepository.save(requirement);
    }

    /**
     * 取消招聘需求
     *
     * @param requirementId 招聘需求ID
     */
    @Transactional
    public void cancelRequirement(String requirementId) {
        RecruitmentRequirement requirement = getRequirementEntity(requirementId);
        requirement.cancel();
        recruitmentRepository.save(requirement);
    }

    /**
     * 安排面试
     *
     * @param requirementId   招聘需求ID
     * @param candidateName   候选人姓名
     * @param candidatePhone  候选人电话
     * @param interviewTime   面试时间
     * @param interviewer     面试官
     * @return 面试信息
     */
    @Transactional
    public Interview scheduleInterview(String requirementId, String candidateName, String candidatePhone,
                                      LocalDateTime interviewTime, String interviewer) {
        RecruitmentRequirement requirement = getRequirementEntity(requirementId);

        if (!requirement.canScheduleMoreInterviews()) {
            throw new IllegalStateException("Cannot schedule more interviews for this requirement");
        }

        Interview interview = requirement.scheduleInterview(
                candidateName,
                candidatePhone,
                interviewTime,
                interviewer
        );

        recruitmentRepository.save(requirement);

        return interview;
    }

    /**
     * 完成面试
     *
     * @param interviewId 面试ID
     * @param result      面试结果
     * @param feedback    面试反馈
     */
    @Transactional
    public void completeInterview(String interviewId, InterviewResult result, String feedback) {
        // 需要通过招聘需求获取面试
        // 简化实现：假设可以直接访问
        List<RecruitmentRequirement> requirements = recruitmentRepository.findByStatus(RecruitmentStatus.APPROVED);

        for (RecruitmentRequirement requirement : requirements) {
            try {
                Interview interview = requirement.getInterviews().stream()
                        .filter(i -> i.getId().value().equals(interviewId))
                        .findFirst()
                        .orElse(null);

                if (interview != null) {
                    interview.complete(result, feedback);
                    recruitmentRepository.save(requirement);

                    // 检查是否招满
                    if (!requirement.canScheduleMoreInterviews()) {
                        requirement.markAsFulfilled();
                        recruitmentRepository.save(requirement);
                    }

                    return;
                }
            } catch (Exception e) {
                // 继续查找
            }
        }

        throw new IllegalArgumentException("Interview not found: " + interviewId);
    }

    /**
     * 获取招聘需求
     *
     * @param id 招聘需求ID
     * @return 招聘需求
     */
    public RecruitmentRequirement getRequirement(String id) {
        return getRequirementEntity(id);
    }

    /**
     * 查询待审批的招聘需求
     *
     * @return 招聘需求列表
     */
    public List<RecruitmentRequirement> findPendingApproval() {
        return recruitmentRepository.findPendingApproval();
    }

    /**
     * 查询已审批且未招满的招聘需求
     *
     * @return 招聘需求列表
     */
    public List<RecruitmentRequirement> findOpenPositions() {
        return recruitmentRepository.findApprovedAndOpen();
    }

    // Helper methods

    private RecruitmentRequirement getRequirementEntity(String id) {
        RecruitmentId requirementId = RecruitmentId.of(id);
        RecruitmentRequirement requirement = recruitmentRepository.findById(requirementId);
        if (requirement == null) {
            throw new IllegalArgumentException("Recruitment requirement not found: " + id);
        }
        return requirement;
    }
}
