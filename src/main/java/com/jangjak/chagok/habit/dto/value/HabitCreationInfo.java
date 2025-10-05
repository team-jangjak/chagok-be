package com.jangjak.chagok.habit.dto.value;

import com.jangjak.chagok.habit.entity.Action;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HabitCreationInfo {
    Long habitId;
    List<Action> actions;
}
