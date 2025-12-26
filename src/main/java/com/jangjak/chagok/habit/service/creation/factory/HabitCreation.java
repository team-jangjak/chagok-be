package com.jangjak.chagok.habit.service.creation.factory;

import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;

import java.time.LocalDateTime;

public interface HabitCreation {
    // 습관 및 액션 생성
    HabitCreationInfo createHabit(HabitCreateRequestDto reqDto, LocalDateTime now);
}
