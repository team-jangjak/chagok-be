package com.jangjak.chagok.habit.enums;

public enum HabitCategory {
    NONE(0),
    HEALTH(1),
    LEARNING(2),
    LIFESTYLE(3),
    ;

    final int value;

    HabitCategory(int value) {
        this.value = value;
    }

    public static HabitCategory fromValue(int value) {
        for (HabitCategory category : HabitCategory.values()) {
            if (category.value == value) {
                return category;
            }
        }
        return NONE;
    }
}
