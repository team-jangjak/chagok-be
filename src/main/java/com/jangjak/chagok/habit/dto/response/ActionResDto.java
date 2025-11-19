package com.jangjak.chagok.habit.dto.response;

import com.jangjak.chagok.common.enums.YN;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActionResDto {
    Long id; //userHabitId
    Long userActionId; //userActionId
    String actionContent; // 액션 내용
    String isCompleted; // completed 여부
}
