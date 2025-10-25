package com.jangjak.chagok.habit.dto.request;

import com.jangjak.chagok.common.anotation.DateTimeFormatter;
import com.jangjak.chagok.habit.dto.request.create.NewActionRequestDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    // ==== 공통 ====
    @DateTimeFormatter
    private LocalDateTime startDate;
    @DateTimeFormatter
    private LocalDateTime endDate;

    private List<NewActionRequestDto> actions;

    // 추후 소셜 공유 기능 고려 진행 중 습관 공개 가능 여부
    private boolean isPublic;

    // ==== 0 (새로 만들기) 해당 ====
    @Positive
    private Long categoryId;

    private boolean isTemplate; // 템플릿 공유 가능한지 여부

    // ==== 0 (새로 만들기), 2 (템플릿 변경 생성) 해당 ===
    private String title;

    private int frequency;

    private int freqUnit;

    // ==== 1, 2 (템플릿 활용) 해당 ====
    @Positive
    private Long habitId;
}
