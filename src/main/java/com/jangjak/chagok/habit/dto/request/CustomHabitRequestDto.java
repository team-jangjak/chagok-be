package com.jangjak.chagok.habit.dto.request;

import com.jangjak.chagok.habit.dto.request.create.NewActionRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomHabitRequestDto {

    // habitId가 있다면 있는 거 재활용
    Long habitId;

    // habitId가 없다면 다시 생성
    Long categoryId;
    String title;
    Integer frequency;
    Integer frequencyUnit;
    Boolean isPublic;
    List<NewActionRequestDto> actions;

    // Non-null 필드
    String startDate;
    String endDate;
}
