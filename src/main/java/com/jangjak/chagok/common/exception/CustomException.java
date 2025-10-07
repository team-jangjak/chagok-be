package com.jangjak.chagok.common.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    HttpStatus status;
    ErrorCode errorCode;

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public CustomException(ErrorCode errorCode) {
        super(errorCode.message);
        this.errorCode = errorCode;
        this.status = errorCode.status;
    }
}
