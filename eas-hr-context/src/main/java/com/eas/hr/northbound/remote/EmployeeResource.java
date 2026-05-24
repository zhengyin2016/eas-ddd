package com.eas.hr.northbound.remote;

import com.eas.hr.message.*;
import com.eas.hr.northbound.appservice.EmployeeAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工REST控制器
 * <p>
 * 这是菱形对称架构中的北向网关 - 开放主机服务(OHS)的一部分。
 * </p>
 */
@RestController
@RequestMapping("/api/hr/employees")
public class EmployeeResource {

    private final EmployeeAppService employeeAppService;

    public EmployeeResource(EmployeeAppService employeeAppService) {
        this.employeeAppService = employeeAppService;
    }

    /**
     * 创建员工
     *
     * @param request 创建请求
     * @return 员工响应
     */
    @PostMapping
    public ApiResponse<EmployeeResponse> createEmployee(@RequestBody CreateEmployeeRequest request) {
        try {
            EmployeeResponse response = employeeAppService.createEmployee(request);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 获取员工信息
     *
     * @param id 员工ID
     * @return 员工响应
     */
    @GetMapping("/{id}")
    public ApiResponse<EmployeeResponse> getEmployee(@PathVariable String id) {
        try {
            EmployeeResponse response = employeeAppService.getEmployee(id);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.notFound(e.getMessage());
        }
    }

    /**
     * 更新员工基本信息
     *
     * @param id      员工ID
     * @param request 更新请求
     * @return 员工响应
     */
    @PutMapping("/{id}")
    public ApiResponse<EmployeeResponse> updateEmployee(
            @PathVariable String id,
            @RequestBody UpdateEmployeeRequest request) {
        try {
            EmployeeResponse response = employeeAppService.updateEmployee(id, request);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 办理入职
     *
     * @param id 员工ID
     * @return 操作结果
     */
    @PostMapping("/{id}/onboard")
    public ApiResponse<Void> onboard(@PathVariable String id) {
        try {
            employeeAppService.onboard(id);
            return ApiResponse.success("Employee onboarded successfully", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 试用期转正
     *
     * @param id 员工ID
     * @return 操作结果
     */
    @PostMapping("/{id}/confirm-probation")
    public ApiResponse<Void> confirmProbation(@PathVariable String id) {
        try {
            employeeAppService.confirmProbation(id);
            return ApiResponse.success("Probation confirmed successfully", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 办理调岗
     *
     * @param id               员工ID
     * @param newDepartmentId 新部门ID
     * @param newPositionId    新岗位ID
     * @return 操作结果
     */
    @PostMapping("/{id}/transfer")
    public ApiResponse<Void> transfer(
            @PathVariable String id,
            @RequestParam String newDepartmentId,
            @RequestParam String newPositionId) {
        try {
            employeeAppService.transferEmployee(id, newDepartmentId, newPositionId);
            return ApiResponse.success("Transfer initiated successfully", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 完成调岗
     *
     * @param id 员工ID
     * @return 操作结果
     */
    @PostMapping("/{id}/complete-transfer")
    public ApiResponse<Void> completeTransfer(@PathVariable String id) {
        try {
            employeeAppService.completeTransfer(id);
            return ApiResponse.success("Transfer completed successfully", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 办理离职
     *
     * @param id     员工ID
     * @param reason 离职原因
     * @return 操作结果
     */
    @PostMapping("/{id}/resign")
    public ApiResponse<Void> resign(
            @PathVariable String id,
            @RequestParam String reason) {
        try {
            employeeAppService.resign(id, reason);
            return ApiResponse.success("Resignation processed successfully", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 添加技能
     *
     * @param id        员工ID
     * @param skillName 技能名称
     * @param level     技能等级
     * @return 操作结果
     */
    @PostMapping("/{id}/skills")
    public ApiResponse<Void> addSkill(
            @PathVariable String id,
            @RequestParam String skillName,
            @RequestParam String level) {
        try {
            employeeAppService.addSkill(id, skillName,
                    com.eas.hr.domain.employee.SkillLevel.valueOf(level));
            return ApiResponse.success("Skill added successfully", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 移除技能
     *
     * @param id        员工ID
     * @param skillName 技能名称
     * @return 操作结果
     */
    @DeleteMapping("/{id}/skills/{skillName}")
    public ApiResponse<Void> removeSkill(
            @PathVariable String id,
            @PathVariable String skillName) {
        try {
            employeeAppService.removeSkill(id, skillName);
            return ApiResponse.success("Skill removed successfully", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 查询可用员工
     *
     * @param query 查询条件
     * @return 可用员工列表
     */
    @PostMapping("/available")
    public ApiResponse<List<AvailableEmployeeResponse>> findAvailable(
            @RequestBody AvailableEmployeeQuery query) {
        List<AvailableEmployeeResponse> response = employeeAppService.findAvailableEmployees(query);
        return ApiResponse.success(response);
    }

    /**
     * 根据部门查询员工
     *
     * @param departmentId 部门ID
     * @return 员工列表
     */
    @GetMapping("/by-department/{departmentId}")
    public ApiResponse<List<EmployeeResponse>> findByDepartment(
            @PathVariable String departmentId) {
        List<EmployeeResponse> response = employeeAppService.findByDepartment(departmentId);
        return ApiResponse.success(response);
    }
}
