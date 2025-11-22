package com.jangjak.chagok.habit.dto.request.create;

import com.jangjak.chagok.common.anotation.DateFormatter;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ActionVerifyRequestDto {
    Long id; // userActionId
    Long checkMethodId;
    @DateFormatter
    LocalDate verifyDate;
    List<String> answer; // 답변
}
