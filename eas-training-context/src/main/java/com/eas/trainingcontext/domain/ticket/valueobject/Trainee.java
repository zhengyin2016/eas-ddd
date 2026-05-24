package com.eas.trainingcontext.domain.ticket.valueobject;

import com.eas.dddcore.ValueObject;

public record Trainee(String employeeId, String name) implements ValueObject {
}
