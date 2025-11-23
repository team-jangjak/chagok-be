package com.jangjak.chagok.habit.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VerifyOfActionResDto {
    String content;
    LocalDateTime verifyDate;
    List<CheckMethodDetailRestDto> details;
}
