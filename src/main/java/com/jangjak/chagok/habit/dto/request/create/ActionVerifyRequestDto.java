package com.jangjak.chagok.habit.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActionVerifyRequestDto {
    // 추후 사이즈 검증 추가
    @NotBlank(message = "답변(answer)은 비어 있을 수 없습니다.")
    String answer; // 답변
}
