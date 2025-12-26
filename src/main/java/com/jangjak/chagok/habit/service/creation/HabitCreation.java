package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.habit.dto.request.create.CreateHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;

import java.time.LocalDateTime;

public interface HabitCreation {
    // 입력값 검증
    boolean validateRequest(Long userId, CreateHabitRequestDto reqDto);
    boolean validateRequest(Long userId, HabitCreateRequestDto reqDto);

    // 습관 및 액션 생성
    HabitCreationInfo createHabit(HabitCreateRequestDto reqDto, LocalDateTime now);

    // 사용자 액션 생성

    // 결제 생성
}
