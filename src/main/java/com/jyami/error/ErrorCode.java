package com.jyami.error;

public enum ErrorCode {

    // 400 BAD_REQUEST
    INVALID_REQUEST(400, "001", "올바르지 않은 인자입니다."),
    PRIVATE_CHANNEL_UPDATE_NOT_SUPPORTED(400, "002", "비공개 채널은 수정할 수 없습니다."),
    PUBLIC_CHANNEL_UPDATE_NOT_SUPPORTED(400, "003", "공개 채널 수정 권한이 없습니다."),
    USER_NOT_FOUND(400, "004", "해당 사용자를 찾을 수 없습니다."),

    UNAUTHORIZED(401, "005", "권한이 없습니다."),

    // 500 server error
    INTERNAL_SERVER_ERROR(500, "006", "Internal Server Error"),
    UNEXPECTED_ERROR(500, "007", "예상치 못한 에러");

    private final int httpStatus;
    private final String code;
    private final String message;

    ErrorCode(int httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
