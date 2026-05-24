package com.eas.hr.northbound.remote;

import com.eas.hr.domain.talent.TalentStatus;
import com.eas.hr.message.*;
import com.eas.hr.northbound.appservice.TalentAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 储备人才REST控制器
 */
@RestController
@RequestMapping("/api/hr/talents")
public class TalentResource {

    private final TalentAppService talentAppService;

    public TalentResource(TalentAppService talentAppService) {
        this.talentAppService = talentAppService;
    }

    /**
     * 创建储备人才
     *
     * @param request 创建请求
     * @return 储备人才响应
     */
    @PostMapping
    public ApiResponse<TalentResponse> createTalent(@RequestBody CreateTalentRequest request) {
        try {
            TalentResponse response = talentAppService.createTalent(request);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 获取储备人才信息
     *
     * @param id 储备人才ID
     * @return 储备人才响应
     */
    @GetMapping("/{id}")
    public ApiResponse<TalentResponse> getTalent(@PathVariable String id) {
        try {
            TalentResponse response = talentAppService.getTalent(id);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.notFound(e.getMessage());
        }
    }

    /**
     * 更新储备人才状态
     *
     * @param id        储备人才ID
     * @param newStatus 新状态
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @PathVariable String id,
            @RequestParam TalentStatus newStatus) {
        try {
            talentAppService.updateStatus(id, newStatus);
            return ApiResponse.success("Status updated successfully", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 将储备人才转化为员工
     *
     * @param id      储备人才ID
     * @param request 创建员工请求
     * @return 员工响应
     */
    @PostMapping("/{id}/convert")
    public ApiResponse<EmployeeResponse> convertToEmployee(
            @PathVariable String id,
            @RequestBody CreateEmployeeRequest request) {
        try {
            EmployeeResponse response = talentAppService.convertToEmployee(id, request);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 查询可转化的储备人才
     *
     * @return 储备人才列表
     */
    @GetMapping("/approved")
    public ApiResponse<List<TalentResponse>> findApprovedForConversion() {
        List<TalentResponse> response = talentAppService.findApprovedForConversion();
        return ApiResponse.success(response);
    }
}
