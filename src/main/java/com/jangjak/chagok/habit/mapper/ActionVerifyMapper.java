package com.jangjak.chagok.habit.mapper;

import com.jangjak.chagok.habit.dto.request.create.ActionVerifyRequestDto;
import com.jangjak.chagok.habit.entity.ActionVerify;

import java.time.LocalDateTime;

public class ActionVerifyMapper {
    public static ActionVerify toEntity(Long userActionId, Long checkMethodId, ActionVerifyRequestDto req) {
        return ActionVerify.builder()
                .userActionId(userActionId)
                .checkMethodId(checkMethodId)
                .verifyDate(LocalDateTime.now())
                .methodOrder(req.getMethodOrder())
                .value(req.getAnswer())
                .build();
    }
}
