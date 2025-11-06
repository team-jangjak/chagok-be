package com.jangjak.chagok.habit.dto.response;

import com.jangjak.chagok.common.enums.YN;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitDashboardResDto {
    // habit
//    Long habitId;
//    String habitTitle; // 습관 타이틀
//    Integer frequency; // 빈도수
    Integer frequencyUnit;
    Long id; //userHabitID
    String categoryName;
    String imageUrl;


    // action
//    Long actionId; // 액션 id
    String actionContent; // 액션 내용
//    Long checkMethodId; // 인증 방식 id
    Integer actionSequence;  // 묶음 순서
    Integer actionFreqSeq;  // 묶음 내부 행위 순서

    // user_action
//    Long userActionId;
    LocalDate actionDate; // 실행 일자
    Integer delayCount; // 미룬 횟수
//    YN isCompleted; // 완료 여부

    Integer progressRate; //진행률

}
