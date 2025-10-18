package com.jangjak.chagok.habit.entity;

import com.jangjak.chagok.common.dto.BaseTimeEntity;
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
public class Action extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long habitId;

    private Long checkMethodId;

    private Integer sequence;

    private String content;

    private Integer freqSeq;
}
