package com.jangjak.chagok.habit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Action {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long habitId;

    private Integer sequence;

    private String content;

    private Integer freqSeq;

    // Enum으로 관리
    private String checkMethod;
}
