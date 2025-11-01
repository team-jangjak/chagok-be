package com.jangjak.chagok.habit.mapper;


import com.jangjak.chagok.habit.dto.request.create.NewActionRequestDto;
import com.jangjak.chagok.habit.dto.request.create.NewHabitRequestDto;
import com.jangjak.chagok.habit.entity.Action;

import java.util.List;

public class ActionMapper {
    public static List<Action> toEntities(Long habitId, NewHabitRequestDto request) {
        return request.getActions().stream()
                .map(value -> Action.builder()
                        .habitId(habitId)                    // 방금 생성된 습관 ID 연결
                        .sequence(value.getSequence())       // 액션 순서
                        .content(value.getContent())         // 액션 내용
                        .freqSeq(value.getFreqSeq())         // 빈도 순서
                        .checkMethodId(value.getCheckMethodId()) // 체크 방법
                        .build()
                )
                .toList();
    }
}
