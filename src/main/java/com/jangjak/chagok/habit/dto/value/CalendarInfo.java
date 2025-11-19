package com.jangjak.chagok.habit.dto.value;

import java.time.LocalDate;

public interface CalendarInfo {
    LocalDate getActionDate();

    Long getUserHabitId();

    Long getUserActionId();

    String getActionContent();

    String getIsCompleted();
}
