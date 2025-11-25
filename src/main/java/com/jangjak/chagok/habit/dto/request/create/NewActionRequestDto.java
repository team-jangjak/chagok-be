package com.jangjak.chagok.habit.dto.request.create;

import com.jangjak.chagok.common.anotation.DateFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewActionRequestDto {
    Integer sequence;
    String content;
    Integer freqSeq;

    Long checkMethodId;

    @DateFormatter
    LocalDate actionDate;
}
