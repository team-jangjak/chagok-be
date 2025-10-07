package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.habit.dto.request.CustomHabitRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;

public abstract class HabitCreationBase {
    public abstract HabitCreationInfo createHabit(CustomHabitRequestDto reqDto);
}
