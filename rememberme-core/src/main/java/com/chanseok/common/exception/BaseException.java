package com.chanseok.common.exception;

public class BaseException extends RuntimeException {
    private static final long serialVersionUID = -166857657035941702L;

    private String errorCode;
    private String errorMessage;

    public BaseException(String errorCode) {
        this(errorCode, null, null);
    }

    public BaseException(String errorCode, String errorMessage) {
        this(errorCode, errorMessage, null);
    }

    public BaseException(String errorCode, String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
