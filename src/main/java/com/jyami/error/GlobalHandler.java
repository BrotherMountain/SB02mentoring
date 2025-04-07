package com.jyami.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalHandler.class); // SLF4J
    private final MessageSource messageSource;

    public GlobalHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(LogicException.class)
    public ResponseEntity<ErrorResponse> handleLogicException(LogicException e) {
        ErrorCode code = e.getErrorCode();
//        String globalMessage = messageSource.getMessage("error.xxx", null, code.getMessage(), null);

        return ResponseEntity.status(code.getHttpStatus())
            .body(new ErrorResponse(code));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(500)
            .body(new ErrorResponse(ErrorCode.UNEXPECTED_ERROR));
    }

}
