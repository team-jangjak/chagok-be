package com.jangjak.chagok.habit.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class CheckMethodDetailCompositeKey implements Serializable {
    @Column(nullable = false)
    private Long checkMethodId;

    @Column(nullable = false)
    private Long methodOrder; // order가 postgresql 예약어라 이름 바꿈

    @Column(nullable = false)
    private LocalDateTime validEndAt;
}
