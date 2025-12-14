package com.jangjak.chagok.habit.entity;

import com.jangjak.chagok.common.dto.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Action extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actionId;

    @Column(nullable = false)
    private Long habitId;

    @Column(nullable = false)
    private Long checkMethodId;

    @Column(nullable = false)
    private Integer sequence;

    private String content;

    @Column(nullable = false)
    private Integer freqSeq;

    @Column(nullable = false)
    private LocalDateTime validStartAt;

    @Column(nullable = false)
    private LocalDateTime validEndAt;

}
