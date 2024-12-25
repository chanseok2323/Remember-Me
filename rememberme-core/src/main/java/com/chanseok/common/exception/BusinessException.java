package com.chanseok.common.exception;

public class BusinessException extends BaseException {
    private static final long serialVersionUID = 1L;

    public BusinessException(String errorCode) {
        super(errorCode);
    }

    public BusinessException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public BusinessException(String errorCode, String errorMessage, Throwable throwable) {
        super(errorCode, errorMessage, throwable);
    }
}