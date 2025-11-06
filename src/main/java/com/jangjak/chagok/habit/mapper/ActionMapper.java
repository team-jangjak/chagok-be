package com.jangjak.chagok.habit.mapper;


import com.jangjak.chagok.habit.dto.request.create.ModifyActionRequestDto;
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

    public static Action toEntity(Long habitId, ModifyActionRequestDto reqAction, Action action) {
        return Action.builder()
                .habitId(habitId)
                .sequence(reqAction.getSequence() != null ? reqAction.getSequence() : action.getSequence())
                .content(reqAction.getContent()  != null ? reqAction.getContent() : action.getContent())
                .freqSeq(reqAction.getFreqSeq()  != null ? reqAction.getFreqSeq() : action.getFreqSeq())
                .checkMethodId(reqAction.getCheckMethodId() != null ? reqAction.getCheckMethodId() : action.getCheckMethodId())
                .build();
    }

    public static Action toEntity(Long habitId, Action action) {
        return Action.builder()
                .habitId(habitId)
                .sequence(action.getSequence())
                .content(action.getContent())
                .freqSeq(action.getFreqSeq())
                .checkMethodId(action.getCheckMethodId())
                .build();

    }

    public static Action toEntity(Long habitId, ModifyActionRequestDto reqAction) {
        return Action.builder()
                .habitId(habitId)                    // 방금 생성된 습관 ID 연결
                .sequence(reqAction.getSequence())       // 액션 순서
                .content(reqAction.getContent())         // 액션 내용
                .freqSeq(reqAction.getFreqSeq())         // 빈도 순서
                .checkMethodId(reqAction.getCheckMethodId()) // 체크 방법
                .build();
    }
}
