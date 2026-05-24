package com.eas.trainingcontext.domain.tickethistory.valueobject;

import com.eas.dddcore.ValueObject;

public record Operator(String employeeId, String name) implements ValueObject {
}
