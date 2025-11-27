package com.jangjak.chagok.admin.dto.info;

import com.jangjak.chagok.habit.enums.HabitState;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HabitProgressDto {
    private Long userHabitId;
    private Long userId;
    private HabitState habitState;  // 습관 상태: IN_PROGRESS, COMPLETED
    private Long totalCount; // userAction 합계
    private Long totalCompleted;  // 완료된(Y) userAction 합계
    private Long untilBaseCount; // 어제까지 예정된 userAction 개수
    private Long untilBaseCompleted; // 어제까지 완료된 userAction 수
}