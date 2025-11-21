package com.jangjak.chagok.habit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HabitCategory {
    EXERCISE(1, "운동"),
    STUDY(2, "공부");

    private final int code;
    private final String value;
}
