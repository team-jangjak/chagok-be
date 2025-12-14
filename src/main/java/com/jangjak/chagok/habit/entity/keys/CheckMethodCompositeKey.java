package com.jangjak.chagok.habit.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class CheckMethodCompositeKey implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkMethodId;

    @Column(nullable = false)
    private LocalDateTime validEndAt;
}
