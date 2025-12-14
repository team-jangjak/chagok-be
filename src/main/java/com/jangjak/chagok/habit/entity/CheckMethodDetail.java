package com.jangjak.chagok.habit.entity;

import com.jangjak.chagok.habit.entity.keys.CheckMethodDetailCompositeKey;
import com.jangjak.chagok.habit.enums.CheckMethodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckMethodDetail {
    @EmbeddedId
    private CheckMethodDetailCompositeKey id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckMethodType type;

    // 추후 LOB 자료형 고려
    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private LocalDateTime validStartAt;
}
