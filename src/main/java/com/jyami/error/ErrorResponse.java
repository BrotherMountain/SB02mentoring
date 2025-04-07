package com.jyami.error;

public record ErrorResponse(String code, String message) {

    public ErrorResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }

}
