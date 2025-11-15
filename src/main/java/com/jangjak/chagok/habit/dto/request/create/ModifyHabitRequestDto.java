package com.jangjak.chagok.habit.dto.request.create;

import com.jangjak.chagok.common.anotation.DateFormatter;
import com.jangjak.chagok.common.anotation.DateTimeFormatter;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ModifyHabitRequestDto implements CreateHabitRequestDto {
    // ==== 공통 ====
    @DateFormatter
    private LocalDate startDate;
    @DateFormatter
    private LocalDate endDate;

    private List<ModifyActionRequestDto> actions;

    @PositiveOrZero
    private Long habitId;

    // 추후 소셜 공유 기능 고려 진행 중 습관 공개 가능 여부
    private boolean isPublic;

    // === null 가능 ===
    private String title;

    private Long categoryId;

    private Integer frequency;

    private Integer freqUnit;

    private Integer freqCount;


}
