package com.eas.crm.northbound.remote;

import com.eas.crm.domain.customer.CustomerLevel;
import com.eas.crm.message.CreateCustomerRequest;
import com.eas.crm.message.CustomerResponse;
import com.eas.crm.northbound.appservice.CustomerAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerResource {

    private final CustomerAppService customerAppService;

    public CustomerResource(CustomerAppService customerAppService) {
        this.customerAppService = customerAppService;
    }

    @PostMapping
    public CustomerResponse createCustomer(@RequestBody CreateCustomerRequest request) {
        return customerAppService.createCustomer(request);
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomer(@PathVariable String id) {
        return customerAppService.getCustomer(id);
    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(
            @PathVariable String id,
            @RequestBody UpdateCustomerRequest request) {
        return customerAppService.updateCustomer(id, request.name(), request.industry(), request.address());
    }

    @GetMapping("/level/{level}")
    public List<CustomerResponse> findByLevel(@PathVariable CustomerLevel level) {
        return customerAppService.findByLevel(level);
    }

    @PostMapping("/{id}/upgrade-level")
    public void upgradeLevel(@PathVariable String id) {
        customerAppService.upgradeLevel(id);
    }

    @PostMapping("/{id}/downgrade-level")
    public void downgradeLevel(@PathVariable String id) {
        customerAppService.downgradeLevel(id);
    }

    @PostMapping("/{id}/contacts")
    public void addContact(
            @PathVariable String id,
            @RequestBody AddContactRequest request) {
        customerAppService.addContact(id, request.name(), request.phone(),
                request.email(), request.position());
    }

    @PostMapping("/{id}/contacts/{contactId}/primary")
    public void setPrimaryContact(@PathVariable String id, @PathVariable String contactId) {
        customerAppService.setPrimaryContact(id, contactId);
    }

    public record UpdateCustomerRequest(String name, String industry, String address) {}
    public record AddContactRequest(String name, String phone, String email, String position) {}
}
