package com.jangjak.chagok.habit.enums;

public enum HabitCreationType {
    NEW(0),
    TEMPLATE(1),
    MODIFY(2),
    ;

    final int value;
    HabitCreationType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static HabitCreationType fromValue(int value) {
        for (HabitCreationType type : HabitCreationType.values()) {
            if (type.value == value) {
                return type;
            }
        }

        return NEW;
    }
}
