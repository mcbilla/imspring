package com.mcb.imspring.web.exception;

/**
 * 500 internal server error.
 */
public class ServerErrorException extends RuntimeException {

    public int statusCode;

    public ServerErrorException(int statusCode) {
        this.statusCode = statusCode;
    }

    public ServerErrorException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ServerErrorException(int statusCode, Throwable cause) {
        super(cause);
        this.statusCode = statusCode;
    }

    public ServerErrorException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
