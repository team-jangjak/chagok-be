package com.jangjak.chagok.habit.dto.value;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgressRateInfo {
    Long userHabitId;
    Integer totalCount;
    Integer completedCount;
}
