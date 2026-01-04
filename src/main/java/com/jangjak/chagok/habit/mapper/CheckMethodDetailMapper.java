package com.jangjak.chagok.habit.mapper;

import com.jangjak.chagok.habit.dto.request.update.CheckMethodUpdateRequestDto;
import com.jangjak.chagok.habit.entity.CheckMethodDetail;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckMethodDetailMapper {

    public static List<CheckMethodDetail> updateFrom(Long checkMethodId, CheckMethodUpdateRequestDto reqDto, LocalDateTime validStDt) {
        return reqDto.getDetails().stream()
                .map(value -> CheckMethodDetail.builder()
                        .checkMethodId(checkMethodId)
                        .methodOrder(value.getMethodOrder())
                        .type(value.getType())
                        .value(value.getValue())
                        .validStartAt(validStDt)
                        .validEndAt(LocalDateTime.of(9999, 12, 31, 23, 59, 59))
                        .build()
                )
                .toList();
    }
}
