package com.eas.pm.southbound.adapter.client;

import com.eas.pm.message.AvailableEmployeeDTO;
import com.eas.pm.southbound.port.client.HRClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * HR上下文ACL适配器实现
 * 调用HR REST API，将HR响应转换为PM DTO
 */
@Component
public class HRClientAdapter implements HRClientPort {

    private final RestTemplate restTemplate;
    private final String hrBaseUrl;

    public HRClientAdapter(RestTemplate restTemplate,
                           @Value("${hr.base-url:http://localhost:8081}") String hrBaseUrl) {
        this.restTemplate = restTemplate;
        this.hrBaseUrl = hrBaseUrl;
    }

    @Override
    public AvailableEmployeeDTO queryEmployee(String employeeId) {
        // 调用HR API: GET /api/hr/employees/{id}
        HREmployeeResponse hrResponse = restTemplate.getForObject(
            hrBaseUrl + "/api/hr/employees/" + employeeId,
            HREmployeeResponse.class
        );

        if (hrResponse == null) {
            throw new IllegalArgumentException("Employee not found: " + employeeId);
        }

        // 转换为PM DTO
        return new AvailableEmployeeDTO(
            hrResponse.id(),
            hrResponse.name(),
            hrResponse.availableRatio(),
            hrResponse.department()
        );
    }

    @Override
    public List<AvailableEmployeeDTO> queryAvailableEmployees(LocalDate startDate, LocalDate endDate) {
        // 调用HR API: GET /api/hr/employees/available?start={start}&end={end}
        HREmployeeResponse[] responses = restTemplate.getForObject(
            hrBaseUrl + "/api/hr/employees/available?start={start}&end={end}",
            HREmployeeResponse[].class,
            startDate, endDate
        );

        if (responses == null) {
            return List.of();
        }

        // 转换为PM DTO列表
        return Arrays.stream(responses)
            .map(hr -> new AvailableEmployeeDTO(
                hr.id(),
                hr.name(),
                hr.availableRatio(),
                hr.department()
            ))
            .toList();
    }

    @Override
    public boolean isEmployeeAvailable(String employeeId, LocalDate startDate, LocalDate endDate) {
        // 调用HR API检查员工可用性
        Boolean available = restTemplate.getForObject(
            hrBaseUrl + "/api/hr/employees/{id}/available?start={start}&end={end}",
            Boolean.class,
            employeeId, startDate, endDate
        );

        return Boolean.TRUE.equals(available);
    }

    /**
     * HR上下文的员工响应（内部DTO，用于接收HR API响应）
     */
    private record HREmployeeResponse(
        String id,
        String name,
        BigDecimal availableRatio,
        String department
    ) {}
}
