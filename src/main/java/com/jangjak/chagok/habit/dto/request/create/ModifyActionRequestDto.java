package com.jangjak.chagok.habit.dto.request.create;

import com.jangjak.chagok.common.anotation.DateFormatter;
import lombok.*;

import java.time.LocalDate;

@Data
public class ModifyActionRequestDto {
    @DateFormatter
    LocalDate actionDate;

    // 변경할 ActionId
    Long actionId;

    // 액션 변경점
    Integer sequence;
    String content;
    Integer freqSeq;
    Long checkMethodId;
}
