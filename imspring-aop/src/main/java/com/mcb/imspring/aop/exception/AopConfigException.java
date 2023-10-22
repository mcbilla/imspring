package com.mcb.imspring.aop.exception;

public class AopConfigException extends RuntimeException{
    public AopConfigException() {
        super();
    }

    public AopConfigException(String message) {
        super(message);
    }

    public AopConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public AopConfigException(Throwable cause) {
        super(cause);
    }
}
