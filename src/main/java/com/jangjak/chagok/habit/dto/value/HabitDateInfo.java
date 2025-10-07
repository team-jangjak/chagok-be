package com.jangjak.chagok.habit.dto.value;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class HabitDateInfo {
    LocalDate startDate;
    LocalDate endDate;
}
