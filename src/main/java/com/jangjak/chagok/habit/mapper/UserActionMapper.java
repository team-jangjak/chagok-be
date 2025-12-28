package com.jangjak.chagok.habit.mapper;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.entity.UserAction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserActionMapper {
    public static UserAction toEntity(Long userId, Long habitId, Long userHabitId, Long actionId, LocalDate actionDate) {
        return UserAction.builder()
                .userId(userId)
                .habitId(habitId)
                .actionId(actionId)
                .userHabitId(userHabitId)
                .actionDate(actionDate)
                .delayCount(0)
                .isCompleted(YN.N)
                .build();
    }
}
