package com.jangjak.chagok.habit.entity.keys;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class HabitCompositeKey implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long habitId;

    @Column(nullable = false)
    private LocalDateTime validEndAt;
}
