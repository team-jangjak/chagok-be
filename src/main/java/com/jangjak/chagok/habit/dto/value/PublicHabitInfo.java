package com.jangjak.chagok.habit.dto.value;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PublicHabitInfo {
    private Long id;
    private String habitImage;
    private Integer habitFreqCount;
    private Integer habitFreqUnit;
    private Integer habitFrequency;
    private String habitTitle;
    private LocalDate habitStartDate;
    private LocalDate habitEndDate;
    private String userName;
    private String userProfileImage;
}
