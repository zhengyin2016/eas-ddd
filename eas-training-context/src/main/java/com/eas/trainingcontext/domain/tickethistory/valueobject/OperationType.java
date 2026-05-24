package com.eas.trainingcontext.domain.tickethistory.valueobject;

import com.eas.dddcore.ValueObject;

public enum OperationType implements ValueObject {
    Nominate,
    Confirm,
    Decline,
    Allocate,
    Cancel
}
