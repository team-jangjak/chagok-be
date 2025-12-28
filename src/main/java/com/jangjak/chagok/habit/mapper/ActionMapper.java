package com.jangjak.chagok.habit.mapper;


import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.request.update.HabitUpdateRequestDto;
import com.jangjak.chagok.habit.entity.Action;

import java.time.LocalDateTime;
import java.util.List;

public class ActionMapper {
    public static List<Action> toEntities(Long habitId, HabitCreateRequestDto request, LocalDateTime validStDt) {
        return request.getActions().stream()
                .map(value -> Action.builder()
                        .habitId(habitId)                    // 방금 생성된 습관 ID 연결
                        .sequence(value.getSequence())       // 액션 순서
                        .content(value.getContent())         // 액션 내용
                        .freqSeq(value.getFreqSeq())         // 빈도 순서
                        .checkMethodId(value.getCheckMethodId()) // 체크 방법
                        .validStartAt(validStDt)
                        .validEndAt(LocalDateTime.MAX)
                        .build()
                )
                .toList();
    }

    public static List<Action> updateFrom(Long habitId, HabitUpdateRequestDto request, LocalDateTime validStDt) {
        return request.getActions().stream()
                .map(value -> Action.builder()
                        .habitId(habitId)                    // 방금 생성된 습관 ID 연결
                        .sequence(value.getSequence())       // 액션 순서
                        .content(value.getContent())         // 액션 내용
                        .freqSeq(value.getFreqSeq())         // 빈도 순서
                        .checkMethodId(value.getCheckMethodId()) // 체크 방법
                        .validStartAt(validStDt)
                        .validEndAt(LocalDateTime.MAX)
                        .build()
                )
                .toList();
    }
}
