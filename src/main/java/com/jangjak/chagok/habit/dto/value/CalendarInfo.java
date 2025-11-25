package com.jangjak.chagok.habit.dto.value;

import com.jangjak.chagok.common.enums.YN;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CalendarInfo {
    LocalDate actionDate;
    Long userHabitId;
    Long userActionId;
    String actionContent;
    YN isCompleted;
}
