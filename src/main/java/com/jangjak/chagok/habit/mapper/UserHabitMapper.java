package com.jangjak.chagok.habit.mapper;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.UserHabit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserHabitMapper {
    public static UserHabit toEntity(HabitCreationInfo info, Long userId, boolean isPublic) {
        return UserHabit.builder()
                .userId(userId)                        // 요청한 사용자 ID
                .habitId(info.getHabitId())       // 습관 ID (기존 또는 새로 생성된)
                .startDate(info.getStartDate())   // 습관 시작일
                .endDate(info.getEndDate())       // 습관 종료일
                .isPublic(isPublic ? YN.Y : YN.N)
                .build();
    }
}
