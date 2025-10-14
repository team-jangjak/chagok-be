package com.jangjak.chagok.habit.dto.value;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PopularCategoryDto", description = "인기 습관 카테고리 항목 DTO")
public class PopularCategoryDto {
    @Schema(description = "카테고리 ID(고유 식별자)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long categoryId;
    @Schema(description = "카테고리 이름", example = "운동", requiredMode = Schema.RequiredMode.REQUIRED)
    String categoryName;
}
