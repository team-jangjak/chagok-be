package com.jangjak.chagok.habit.dto.request.create;

import com.jangjak.chagok.common.anotation.DateFormatter;
import com.jangjak.chagok.common.anotation.DateTimeFormatter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TemplateHabitRequestDto implements  CreateHabitRequestDto {
    // ==== 공통 ====
    @DateFormatter
    private LocalDate startDate;
    @DateFormatter
    private LocalDate endDate;

    @NotNull
    private List<TemplateActionRequestDto> actions;

    // 추후 소셜 공유 기능 고려 진행 중 사용자 습관 공개 가능 여부
    @NotNull
    private Boolean isPublic;

    @NotNull @Positive
    private Long habitId;
}
