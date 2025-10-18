package com.jangjak.chagok.habit.entity;

import com.jangjak.chagok.common.dto.BaseTimeEntity;
import com.jangjak.chagok.common.enums.YN;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Habit extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long categoryId;

    private String title;

    private Integer frequency;

    private Integer freqUnit;

    // Y: 공개, N: 비공개
    @Enumerated(EnumType.STRING)
    private YN isPublic;
}
