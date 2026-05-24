package com.eas.crm.southbound.adapter.client;

import com.eas.crm.southbound.port.client.PMClientPort;
import com.eas.crm.southbound.port.client.pm.CreateProjectCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * PM上下文ACL适配器
 * 负责在合同签订后通知PM上下文创建关联项目
 * 目前是模拟实现，实际需要调用PM上下文的REST API
 */
@Component
public class PMClientAdapter implements PMClientPort {

    private static final Logger log = LoggerFactory.getLogger(PMClientAdapter.class);

    @Override
    public void createProject(CreateProjectCommand command) {
        // 模拟调用PM上下文API创建项目
        log.info("Creating project in PM context: name={}, customerId={}, contractId={}, budget={}",
                command.name(), command.customerId(), command.contractId(), command.budget());

        // TODO: 实际实现应该调用PM上下文的REST API
        // RestTemplate.postForEntity("http://pm-context/api/projects", toRequest(command), Void.class);

        log.info("Project created successfully in PM context");
    }
}
