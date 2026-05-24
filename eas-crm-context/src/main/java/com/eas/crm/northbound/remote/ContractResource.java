package com.eas.crm.northbound.remote;

import com.eas.crm.message.ContractResponse;
import com.eas.crm.message.CreateContractRequest;
import com.eas.crm.northbound.appservice.ContractAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractResource {

    private final ContractAppService contractAppService;

    public ContractResource(ContractAppService contractAppService) {
        this.contractAppService = contractAppService;
    }

    @PostMapping
    public ContractResponse createContract(@RequestBody CreateContractRequest request) {
        return contractAppService.createContract(request);
    }

    @GetMapping("/{id}")
    public ContractResponse getContract(@PathVariable String id) {
        return contractAppService.getContract(id);
    }

    @PostMapping("/{id}/submit-review")
    public ContractResponse submitForReview(@PathVariable String id) {
        return contractAppService.submitForReview(id);
    }

    @PostMapping("/{id}/approve")
    public ContractResponse approve(
            @PathVariable String id,
            @RequestBody ApproveRequest request) {
        return contractAppService.approve(id, request.approverId());
    }

    @PostMapping("/{id}/reject")
    public ContractResponse reject(
            @PathVariable String id,
            @RequestBody RejectRequest request) {
        return contractAppService.reject(id, request.reason());
    }

    @PostMapping("/{id}/activate")
    public ContractResponse activate(@PathVariable String id) {
        return contractAppService.activate(id);
    }

    @PostMapping("/{id}/complete")
    public ContractResponse complete(@PathVariable String id) {
        return contractAppService.complete(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<ContractResponse> findByCustomerId(@PathVariable String customerId) {
        return contractAppService.findByCustomerId(customerId);
    }

    @PostMapping("/{id}/payment-plans")
    public void createPaymentPlan(
            @PathVariable String id,
            @RequestBody CreatePaymentPlanRequest request) {
        contractAppService.createPaymentPlan(id, request.plans());
    }

    public record ApproveRequest(String approverId) {}
    public record RejectRequest(String reason) {}
    public record CreatePaymentPlanRequest(List<ContractAppService.PaymentPlanRequest> plans) {}
}
