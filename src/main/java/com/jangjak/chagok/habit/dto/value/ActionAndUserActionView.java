package com.jangjak.chagok.habit.dto.value;

import java.time.LocalDate;

// 읽기 전용 Projection
public interface ActionAndUserActionView {

    String getHabitTitle();

    Integer getFrequency();

    Long getHabitId();

    Integer getCategoryId();

    // action
    Long getActionId();

    Long getCheckMethodId();

    // user_action
    Long getUserActionId();

    Long getUserHabitId();

    String getIsCompleted();

    // HabitDashboardResDto
    Integer getFrequencyUnit();

    String getImage();

    String getActionContent();

    Integer getActionSequence();

    Integer getActionFreqSeq();

    LocalDate getActionDate();

    Integer getDelayCount();

}
