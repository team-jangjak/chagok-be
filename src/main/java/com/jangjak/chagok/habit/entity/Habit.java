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


    public static Habit.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long habitId = null;
        private Long category;
        private String title;
        private Integer frequency;
        private Integer freqUnit;
        private Integer freqCount;
        private String image;
        private LocalDateTime validStartAt;
        private LocalDateTime validEndAt;
        private YN isTemplate;

        Builder() {}

        public Builder category(Long category) {
            this.category = category;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder frequency(Integer frequency) {
            this.frequency = frequency;
            return this;
        }

        public Builder freqUnit(Integer freqUnit) {
            this.freqUnit = freqUnit;
            return this;
        }

        public Builder freqCount(Integer freqCount) {
            this.freqCount = freqCount;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder validStartAt(LocalDateTime validStartAt) {
            this.validStartAt = validStartAt;
            return this;
        }

        public Builder validEndAt(LocalDateTime validEndAt) {
            this.validEndAt = validEndAt;
            return this;
        }

        public Builder isTemplate(YN isTemplate) {
            this.isTemplate = isTemplate;
            return this;
        }

        public Habit build() {
            return new Habit(
                    new HabitCompositeKey(habitId, validEndAt),
                    category,
                    title,
                    frequency,
                    freqUnit,
                    freqCount,
                    image,
                    isTemplate,
                    validStartAt
            );
        }
    }
}
