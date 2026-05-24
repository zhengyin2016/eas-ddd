package com.eas.pm.northbound.remote;

import com.eas.pm.message.AssignMemberRequest;
import com.eas.pm.message.AvailableEmployeeDTO;
import com.eas.pm.northbound.appservice.AssignmentAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 人员分配REST控制器
 */
@RestController
@RequestMapping("/api/pm/assignments")
public class AssignmentResource {

    private final AssignmentAppService assignmentAppService;

    public AssignmentResource(AssignmentAppService assignmentAppService) {
        this.assignmentAppService = assignmentAppService;
    }

    @PostMapping
    public void assignMember(@RequestBody AssignMemberRequest request) {
        assignmentAppService.assignMember(request);
    }

    @PostMapping("/{id}/release")
    public void releaseMember(@PathVariable String id) {
        assignmentAppService.releaseMember(id);
    }

    @PutMapping("/{id}/allocation")
    public void updateAllocation(@PathVariable String id, @RequestParam int allocation) {
        assignmentAppService.updateAllocation(id, allocation);
    }

    @GetMapping("/available")
    public List<AvailableEmployeeDTO> queryAvailableResources(@RequestParam String projectId) {
        return assignmentAppService.queryAvailableResources(projectId);
    }
}
