package com.jangjak.chagok.habit.dto.value;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularCategoryDto {
    Long categoryId;
    String categoryName;
}
