package com.jangjak.chagok.habit.mapper;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.UserHabit;
import com.jangjak.chagok.habit.enums.HabitState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserHabitMapper {
    public static UserHabit toEntity(HabitCreationInfo info, Long userId, HabitCreateRequestDto reqDto) {
        return UserHabit.builder()
                .userId(userId)                        // 요청한 사용자 ID
                .habitId(info.getHabitId())       // 습관 ID (기존 또는 새로 생성된)
                .startDate(reqDto.getHabitStartDate())   // 습관 시작일
                .endDate(reqDto.getHabitEndDate())       // 습관 종료일
                .isPublic(reqDto.getIsPublic() ? YN.Y : YN.N)
                .state(!LocalDate.now(ZoneId.of("Asia/Seoul")).isAfter(reqDto.getHabitStartDate()) ? HabitState.IN_PROGRESS : HabitState.PENDING)
                .build();
    }
}
