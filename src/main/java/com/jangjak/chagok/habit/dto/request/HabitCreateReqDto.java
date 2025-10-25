package com.jangjak.chagok.habit.dto.request;

import com.jangjak.chagok.common.anotation.DateTimeFormatter;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HabitCreateReqDto {
    // 습관 생성 3가지 유형
    // 0: 맨땅 습관 만들기 (isPublic = 선택사항)
    // 1: 있는 거 그대로 가져다 쓰기 (isPublic = false)
    // 2: 있는 거 변형해서 쓰기 (isPublic = false)
    @NotNull @Range(min = 0, max = 2)
    private int createType;

    // 공통
    @DateTimeFormatter
    private LocalDateTime startDate;
    @DateTimeFormatter
    private LocalDateTime endDate;

    // 0 해당
    private Long categoryId;
    private String title;
    private int frequency;
    private int freqUnit;
    private boolean isPublic;
    private List<ActionRequestDto> actions;

    // 1, 2 해당
    private Long habitId;





}
