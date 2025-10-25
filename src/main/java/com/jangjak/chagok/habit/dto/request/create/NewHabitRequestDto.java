package com.jangjak.chagok.habit.dto.request.create;

import com.jangjak.chagok.common.anotation.DateFormatter;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class NewHabitRequestDto implements CreateHabitRequestDto {
    @DateFormatter
    private LocalDate startDate;
    @DateFormatter
    private LocalDate endDate;

    private List<NewActionRequestDto> actions;

    // 추후 소셜 공유 기능 고려 진행 중 습관 공개 가능 여부
    private boolean isPublic;

    // 템플릿 공유 가능한지 여부
    private boolean isTemplate;

    @Positive
    private Long categoryId;

    private String title;

    private int frequency;

    private int freqUnit;
}
