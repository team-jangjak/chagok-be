package com.jangjak.chagok.habit.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CalendarViewResDto {
    LocalDate actionDate;
    List<ActionResDto> actionResDto;
}
