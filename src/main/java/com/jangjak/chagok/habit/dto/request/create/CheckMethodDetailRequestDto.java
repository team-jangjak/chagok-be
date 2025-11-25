package com.jangjak.chagok.habit.dto.request.create;

import com.jangjak.chagok.habit.enums.CheckMethodType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckMethodDetailRequestDto {
    @NotNull(message = "methodOrder는 필수 값입니다.")
    @Positive(message = "methodOrder는 1 이상의 정수여야 합니다.")
    Long methodOrder;

    @NotNull(message = "type은 필수 값입니다.")
    CheckMethodType type;

    @NotBlank(message = "value는 비어 있을 수 없습니다.")
    String value;
}
