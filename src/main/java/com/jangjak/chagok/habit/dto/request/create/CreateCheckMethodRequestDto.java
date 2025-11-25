package com.jangjak.chagok.habit.dto.request.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateCheckMethodRequestDto {
    @NotBlank(message = "value는 비어 있을 수 없습니다.")
    String title;

    @NotEmpty(message = "details는 최소 1개 이상이어야 합니다.")
    @Valid
    List<CheckMethodDetailRequestDto> details;
}
