package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.habit.dto.request.create.CreateHabitRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;

public interface HabitCreation {
    // 입력값 검증
    boolean validateRequest(Long userId, CreateHabitRequestDto reqDto);

    // 습관 및 액션 생성
    HabitCreationInfo createHabit(CreateHabitRequestDto reqDto);

    // 사용자 액션 생성
    Long createUserHabit(Long userId, CreateHabitRequestDto reqDto, HabitCreationInfo habitInfo);

    // 결제 생성
}
