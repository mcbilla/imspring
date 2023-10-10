package com.mcb.imspring.core.exception;

/**
 * 框架异常基类
 */
public class NestedRuntimeException extends RuntimeException {
    public NestedRuntimeException() {
        super();
    }

    public NestedRuntimeException(String message) {
        super(message);
    }

    public NestedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NestedRuntimeException(Throwable cause) {
        super(cause);
    }
}
