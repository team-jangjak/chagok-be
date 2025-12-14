package com.jangjak.chagok.habit.entity;

import com.jangjak.chagok.common.dto.BaseTimeEntity;
import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.habit.entity.keys.HabitCompositeKey;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Habit extends BaseTimeEntity {
    @EmbeddedId
    private HabitCompositeKey id;

    @Column(nullable = false)
    private Long category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer frequency;

    @Column(nullable = false)
    private Integer freqUnit;

    @Column(nullable = false)
    private Integer freqCount;

    private String image;

    // Y: 공개, N: 비공개
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private YN isTemplate;

    @Column(nullable = false)
    private LocalDateTime validStartAt;

}
