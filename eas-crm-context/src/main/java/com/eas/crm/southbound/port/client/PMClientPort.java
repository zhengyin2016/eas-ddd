package com.eas.crm.southbound.port.client;

import com.eas.crm.southbound.port.client.pm.CreateProjectCommand;

public interface PMClientPort {
    void createProject(CreateProjectCommand command);
}
