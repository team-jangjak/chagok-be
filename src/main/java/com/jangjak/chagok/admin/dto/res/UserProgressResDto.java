package com.jangjak.chagok.admin.dto.res;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProgressResDto {
    private Long id;  // userId
    private String name;
    private Double avgProgress;  // 수행률
    private Integer habitCount;  // 습관 개수
    private LocalDateTime calculatedAt;
}
