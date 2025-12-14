package com.jangjak.chagok.habit.entity;

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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkMethodDetailId;

    private Long checkMethodId;

    @Column(nullable = false)
    private Long methodOrder; // order가 postgresql 예약어라 이름 바꿈

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckMethodType type;

    // 추후 LOB 자료형 고려
    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private LocalDateTime validStartAt;

    @Column(nullable = false)
    private LocalDateTime validEndAt;
}
