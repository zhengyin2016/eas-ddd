package com.eas.crm.northbound.remote;

import com.eas.crm.message.PaymentResponse;
import com.eas.crm.message.RecordPaymentRequest;
import com.eas.crm.northbound.appservice.PaymentAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentResource {

    private final PaymentAppService paymentAppService;

    public PaymentResource(PaymentAppService paymentAppService) {
        this.paymentAppService = paymentAppService;
    }

    @PostMapping
    public PaymentResponse recordPayment(@RequestBody RecordPaymentRequest request) {
        return paymentAppService.recordPayment(request);
    }

    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable String id) {
        return paymentAppService.getPayment(id);
    }

    @PostMapping("/{id}/confirm")
    public PaymentResponse confirmPayment(@PathVariable String id) {
        return paymentAppService.confirmPayment(id);
    }

    @GetMapping("/contract/{contractId}")
    public List<PaymentResponse> findByContractId(@PathVariable String contractId) {
        return paymentAppService.findByContractId(contractId);
    }
}
