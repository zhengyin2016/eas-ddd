package com.eas.dddcore;

/**
 * 领域异常
 * 用于表达领域规则违反的运行时异常。
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
