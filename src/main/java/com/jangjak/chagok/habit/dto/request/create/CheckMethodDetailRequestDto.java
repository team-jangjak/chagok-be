package com.jangjak.chagok.habit.dto.request.create;

import com.jangjak.chagok.habit.enums.CheckMethodType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckMethodDetailRequestDto {
    Long methodOrder;
    CheckMethodType type;
    String value;
}
