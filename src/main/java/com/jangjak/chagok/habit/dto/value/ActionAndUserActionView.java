package com.jangjak.chagok.habit.dto.value;

import java.time.LocalDate;

// 읽기 전용 Projection
public interface ActionAndUserActionView {

    Integer getFrequency();

    Long getHabitId();

    // action
    Long getActionId();

    Long getCheckMethodId();

    // user_action
    Long getUserActionId();

    Long getUserHabitId();

    String getIsCompleted();

    // HabitDashboardResDto
    String getImage();

    String getActionContent();

    Integer getActionSequence();

    Integer getActionFreqSeq();

    LocalDate getActionDate();

    Integer getDelayCount();

}
