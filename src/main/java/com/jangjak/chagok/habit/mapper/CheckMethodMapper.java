package com.jangjak.chagok.habit.mapper;

import com.jangjak.chagok.habit.dto.request.update.CheckMethodUpdateRequestDto;
import com.jangjak.chagok.habit.entity.CheckMethod;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckMethodMapper {

    public static CheckMethod updateFrom(CheckMethod existing, CheckMethodUpdateRequestDto reqDto, LocalDateTime validStDt) {
        return CheckMethod.builder()
                .checkMethodId(existing.getId().getCheckMethodId())
                .userId(existing.getUserId())
                .title(reqDto.getTitle() != null ? reqDto.getTitle() : existing.getTitle())
                .validStartAt(validStDt)
                .validEndAt(LocalDateTime.of(9999, 12, 31, 23, 59, 59))
                .build();
    }
}
