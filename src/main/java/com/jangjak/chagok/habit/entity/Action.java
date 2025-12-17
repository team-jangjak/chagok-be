package com.jangjak.chagok.habit.entity;

import com.jangjak.chagok.common.dto.BaseTimeEntity;
import com.jangjak.chagok.habit.entity.keys.ActionCompositeKey;
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
    @EmbeddedId
    private ActionCompositeKey id; // 복합키 설정

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
}
