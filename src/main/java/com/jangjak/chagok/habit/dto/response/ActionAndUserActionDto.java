package com.jangjak.chagok.habit.dto.response;

import com.jangjak.chagok.common.enums.YN;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionAndUserActionDto {
    // action
    Long habitId;
    Long actionId;
    String ActionContent; // 액션 내용
    Long checkMethodId; // 인증 방식 id
    Integer ActionSequence;  // 묶음 순서
    Integer ActionFreqSeq;  // 묶음 내부 행위 순서

    // user_action
    Long userActionId;
    Long userHabitId;
    LocalDate actionDate; // 실행 일자
    Integer delayCount; // 미룬 횟수
    YN isCompleted; // 완료 여부

}

