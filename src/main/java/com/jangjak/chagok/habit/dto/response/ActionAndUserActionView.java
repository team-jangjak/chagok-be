package com.jangjak.chagok.habit.dto.response;

import java.time.LocalDate;

// 읽기 전용 Projection
public interface ActionAndUserActionView {

    // action
    Long getHabitId();

    Long getActionId();

    String getActionContent();

    Long getCheckMethodId();

    Integer getActionSequence();

    Integer getActionFreqSeq();

    // user_action
    Long getUserActionId();

    Long getUserHabitId();

    LocalDate getActionDate();

    Integer getDelayCount();

    String getIsCompleted();
}
