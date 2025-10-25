package com.jangjak.chagok.habit.dto.request.create;

import com.jangjak.chagok.common.anotation.DateTimeFormatter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TemplateHabitRequestDto {
    // ==== 공통 ====
    @DateTimeFormatter
    private LocalDateTime startDate;
    @DateTimeFormatter
    private LocalDateTime endDate;

    private List<TemplateActionRequestDto> actions;

    // 추후 소셜 공유 기능 고려 진행 중 사용자 습관 공개 가능 여부
    private boolean isPublic;

    @Positive
    private Long habitId;
}
