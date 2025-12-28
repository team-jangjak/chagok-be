package com.jangjak.chagok.habit.dto.value;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.entity.UserHabit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class HabitCreationInfo {
    Long habitId;
    List<Action> actions;
}
