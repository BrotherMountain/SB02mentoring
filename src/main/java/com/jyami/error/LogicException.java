package com.jyami.error;

public class LogicException extends RuntimeException {
    private final ErrorCode errorCode;

    public LogicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
