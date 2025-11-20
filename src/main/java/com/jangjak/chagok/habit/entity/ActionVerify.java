package com.jangjak.chagok.habit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActionVerify {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long checkMethodId;

    private LocalDate verifyDate;

    // 얘는 진짜 LOB 자료형 해야될수도
    private String value;
}
