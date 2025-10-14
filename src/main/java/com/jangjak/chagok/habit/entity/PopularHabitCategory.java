package com.jangjak.chagok.habit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "popular_habit_category")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopularHabitCategory {
    @Id
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "usage_count", nullable = false)
    private Long usageCount;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;
}