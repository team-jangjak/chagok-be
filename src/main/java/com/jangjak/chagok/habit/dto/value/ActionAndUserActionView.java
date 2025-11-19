package com.jangjak.chagok.habit.dto.value;

import java.time.LocalDate;

// 읽기 전용 Projection
public interface ActionAndUserActionView {

    String getHabitTitle();
    Integer getFrequency();
    Long getHabitId();
    String getCategoryName();
    Long getCategoryId();
    // action
    Long getActionId();
    Long getCheckMethodId();

    // user_action
    Long getUserActionId();

    Long getUserHabitId();

    String getIsCompleted();

    // habit dashboard res dto
    Integer getFrequencyUnit();
    Long getId();                 // userHabitId
    String getImage();

    String getActionContent();
    Integer getActionSequence();
    Integer getActionFreqSeq();

    LocalDate getActionDate();
    Integer getDelayCount();

    //
    Integer getProgressRate();
}
