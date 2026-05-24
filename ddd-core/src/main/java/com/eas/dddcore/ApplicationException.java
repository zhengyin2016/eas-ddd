package com.eas.dddcore;

/**
 * 应用层异常
 * 用于表达应用服务层业务逻辑错误的运行时异常。
 */
public class ApplicationException extends RuntimeException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
