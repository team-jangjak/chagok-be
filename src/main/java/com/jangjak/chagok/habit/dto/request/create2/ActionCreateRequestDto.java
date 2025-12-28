package com.jangjak.chagok.habit.dto.request.create2;

import com.jangjak.chagok.common.anotation.DateTimeFormatter;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActionCreateRequestDto {
    private Long checkMethodId;         // 인증방식 ID
    private Integer sequence;           // 행위가 속한 습관 빈도 단위의 순서
    private String content;             // 행위 내용
    private Integer freqSeq;            // 습관 빈도 단위 내 행위의 순서

    @DateTimeFormatter
    private LocalDateTime actionDate;   // 행위 수행일

    private Long templateActionId;      // 템플릿 행위 ID (템플릿이 존재하고 기존 행위와 변경된 것이 없다면 행위 ID 추가)
}
