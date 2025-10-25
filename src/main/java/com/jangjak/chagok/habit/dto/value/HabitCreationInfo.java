package com.jangjak.chagok.habit.dto.value;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.entity.UserHabit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class HabitCreationInfo {
    Long habitId;
    List<Action> actions;

    LocalDate startDate;
    LocalDate endDate;

    public UserHabit toUserHabit(Long userId, boolean isPublic) {
        return UserHabit.builder()
                .userId(userId)                        // 요청한 사용자 ID
                .habitId(this.getHabitId())       // 습관 ID (기존 또는 새로 생성된)
                .startDate(this.getStartDate())   // 습관 시작일
                .endDate(this.getEndDate())       // 습관 종료일
                .isPublic(isPublic ? YN.Y : YN.N)
                .build();
    }
}
