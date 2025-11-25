package com.jangjak.chagok.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOT_FOUND("요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BAD_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("잘못된 접근입니다.", HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR("서버 내부 오류입니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    DATE_FORMAT_ERROR("잘못된 날짜 형식입니다. 날짜는 yyyyMMdd 형식으로 전달해주세요.", HttpStatus.BAD_REQUEST),
    DATASET_ERROR("유효한 요청이지만 데이터를 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_ACTION_DATE("습관 인증 가능일이 아닙니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_VERIFIED("이미 인증된 데이터입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    AI_RESPONSE_ERROR("AI 응답이 비어 있습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    AI_RESPONSE_MAPPING_ERROR("AI 계획 생성 실패: 결과가 비어 있습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    AI_REQUEST_ERROR("AI 계획 생성 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    final String message;
    final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
