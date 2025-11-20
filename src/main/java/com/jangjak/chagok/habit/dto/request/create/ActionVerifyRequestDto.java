package com.jangjak.chagok.habit.dto.request.create;

import com.jangjak.chagok.common.anotation.DateFormatter;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ActionVerifyRequestDto {
    // id, checkMethodId, date, value
    Long id; // userActionId
    Long checkMethodId;
    @DateFormatter
    LocalDate verifyDate;
    String value;
}
