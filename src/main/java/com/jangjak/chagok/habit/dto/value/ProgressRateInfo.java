package com.jangjak.chagok.habit.dto.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProgressRateInfo {
    Long userHabitId;
    Long totalCount;
    Long completedCount;
}
