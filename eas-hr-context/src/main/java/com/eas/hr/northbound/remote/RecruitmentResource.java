package com.eas.hr.northbound.remote;

import com.eas.hr.domain.recruitment.Interview;
import com.eas.hr.domain.recruitment.InterviewResult;
import com.eas.hr.domain.recruitment.RecruitmentRequirement;
import com.eas.hr.message.ApiResponse;
import com.eas.hr.northbound.appservice.RecruitmentAppService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 招聘REST控制器
 */
@RestController
@RequestMapping("/api/hr/recruitment")
public class RecruitmentResource {

    private final RecruitmentAppService recruitmentAppService;

    public RecruitmentAppService getRecruitmentAppService() {
        return recruitmentAppService;
    }

    public RecruitmentResource(RecruitmentAppService recruitmentAppService) {
        this.recruitmentAppService = recruitmentAppService;
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
     * @return 招聘需求
     */
    @PostMapping("/requirements")
    public ApiResponse<RecruitmentRequirement> submitRequirement(
            @RequestParam String title,
            @RequestParam String departmentId,
            @RequestParam String positionId,
            @RequestParam int count,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String requirements,
            @RequestParam String createdBy) {
        try {
            RecruitmentRequirement requirement = recruitmentAppService.submitRequirement(
                    title, departmentId, positionId, count, description, requirements, createdBy);
            return ApiResponse.success(requirement);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 获取招聘需求
     *
     * @param id 招聘需求ID
     * @return 招聘需求
     */
    @GetMapping("/requirements/{id}")
    public ApiResponse<RecruitmentRequirement> getRequirement(@PathVariable String id) {
        try {
            RecruitmentRequirement requirement = recruitmentAppService.getRequirement(id);
            return ApiResponse.success(requirement);
        } catch (IllegalArgumentException e) {
            return ApiResponse.notFound(e.getMessage());
        }
    }

    /**
     * 审批通过招聘需求
     *
     * @param id       招聘需求ID
     * @param approver 审批人
     * @return 操作结果
     */
    @PostMapping("/requirements/{id}/approve")
    public ApiResponse<Void> approveRequirement(
            @PathVariable String id,
            @RequestParam String approver) {
        try {
            recruitmentAppService.approveRequirement(id, approver);
            return ApiResponse.success("Requirement approved successfully", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 审批拒绝招聘需求
     *
     * @param id       招聘需求ID
     * @param approver 审批人
     * @param reason   拒绝原因
     * @return 操作结果
     */
    @PostMapping("/requirements/{id}/reject")
    public ApiResponse<Void> rejectRequirement(
            @PathVariable String id,
            @RequestParam String approver,
            @RequestParam(required = false) String reason) {
        try {
            recruitmentAppService.rejectRequirement(id, approver, reason);
            return ApiResponse.success("Requirement rejected successfully", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
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
    @PostMapping("/requirements/{requirementId}/interviews")
    public ApiResponse<Interview> scheduleInterview(
            @PathVariable String requirementId,
            @RequestParam String candidateName,
            @RequestParam String candidatePhone,
            @RequestParam LocalDateTime interviewTime,
            @RequestParam String interviewer) {
        try {
            Interview interview = recruitmentAppService.scheduleInterview(
                    requirementId, candidateName, candidatePhone, interviewTime, interviewer);
            return ApiResponse.success(interview);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 完成面试
     *
     * @param interviewId 面试ID
     * @param result      面试结果
     * @param feedback    面试反馈
     * @return 操作结果
     */
    @PostMapping("/interviews/{interviewId}/complete")
    public ApiResponse<Void> completeInterview(
            @PathVariable String interviewId,
            @RequestParam InterviewResult result,
            @RequestParam(required = false) String feedback) {
        try {
            recruitmentAppService.completeInterview(interviewId, result, feedback);
            return ApiResponse.success("Interview completed successfully", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 查询待审批的招聘需求
     *
     * @return 招聘需求列表
     */
    @GetMapping("/requirements/pending")
    public ApiResponse<List<RecruitmentRequirement>> findPendingApproval() {
        List<RecruitmentRequirement> requirements = recruitmentAppService.findPendingApproval();
        return ApiResponse.success(requirements);
    }

    /**
     * 查询已审批且未招满的招聘需求
     *
     * @return 招聘需求列表
     */
    @GetMapping("/positions/open")
    public ApiResponse<List<RecruitmentRequirement>> findOpenPositions() {
        List<RecruitmentRequirement> requirements = recruitmentAppService.findOpenPositions();
        return ApiResponse.success(requirements);
    }
}
