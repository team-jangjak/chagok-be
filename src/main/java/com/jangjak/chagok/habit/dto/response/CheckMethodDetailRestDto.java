package com.jangjak.chagok.habit.dto.response;

import com.jangjak.chagok.habit.enums.CheckMethodType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckMethodDetailRestDto {
    Long methodOrder;
    CheckMethodType type;
    String value;  // 질문
    String answer;
}
