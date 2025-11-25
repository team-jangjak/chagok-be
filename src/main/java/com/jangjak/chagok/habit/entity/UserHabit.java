package com.jangjak.chagok.habit.entity;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.enums.HabitState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
public class UserHabit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long habitId;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private YN isPublic;

    @Enumerated(EnumType.STRING)
    private HabitState state;
}
