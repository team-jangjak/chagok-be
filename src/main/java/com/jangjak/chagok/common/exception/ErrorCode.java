package com.jangjak.chagok.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOT_FOUND("요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BAD_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("잘못된 접근입니다.", HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR("서버 내부 오류입니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    final String message;
    final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
