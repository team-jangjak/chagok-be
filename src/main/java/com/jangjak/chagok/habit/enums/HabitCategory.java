package com.jangjak.chagok.habit.enums;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum HabitCategory {
    EXERCISE(1, "운동"),
    STUDY(2, "공부");

    private final int code;
    private final String value;

    public static HabitCategory toEnum(int code) {
        return Arrays.stream(values())
            .filter(c -> c.code == code)
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}
