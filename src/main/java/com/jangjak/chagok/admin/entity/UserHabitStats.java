package com.jangjak.chagok.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_habit_stats")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserHabitStats {

    @Id
    private Long userId;

    private Integer habitCount;   // 사용자의 습관 개수

    private Double avgProgress;   // 수행률

    private LocalDateTime calculatedAt;

    public static UserHabitStats create(Long userId, Integer habitCount, Double avgProgress) {
        return new UserHabitStats(
                userId,
                habitCount,
                avgProgress,
                LocalDateTime.now()
        );
    }

}