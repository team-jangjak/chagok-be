package com.jangjak.chagok.habit.enums;

import java.util.Locale;
import java.util.Objects;

public enum HabitCategory {
    NONE(0L),
    HEALTH(1L),
    LEARNING(2L),
    LIFESTYLE(3L),
    OTHER(4L),
    ;

    final Long value;

    HabitCategory(Long value) {
        this.value = value;
    }

    public Long value() {
        return value;
    }

    public static HabitCategory fromValue(int value) {
        Long valueLong = (long) value;
        for (HabitCategory category : HabitCategory.values()) {
            if (Objects.equals(category.value, valueLong)) {
                return category;
            }
        }
        return NONE;
    }

    public static HabitCategory fromName(String name) {
        for (HabitCategory category : HabitCategory.values()) {
            if (category.name().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return NONE;
    }
}
