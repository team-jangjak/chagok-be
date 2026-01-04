package com.jangjak.chagok.habit.entity;

import com.jangjak.chagok.common.dto.BaseTimeEntity;
import com.jangjak.chagok.habit.entity.keys.ActionCompositeKey;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
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

    public static Action.Builder builder() {
        return new Action.Builder();
    }

    public static class Builder {
        private Long actionId = null;
        private Long habitId;
        private Long checkMethodId;
        private Integer sequence;
        private String content;
        private Integer freqSeq;
        private LocalDateTime validStartAt;
        private LocalDateTime validEndAt = LocalDateTime.MAX;

        public Action build() {
            return new Action(
                    new ActionCompositeKey(actionId, validEndAt),
                    habitId,
                    checkMethodId,
                    sequence,
                    content,
                    freqSeq,
                    validStartAt
            );
        }

        public Builder habitId(Long habitId) {
            this.habitId = habitId;
            return this;
        }

        public Builder checkMethodId(Long checkMethodId) {
            this.checkMethodId = checkMethodId;
            return this;
        }

        public Builder freqSeq(Integer freqSeq) {
            this.freqSeq = freqSeq;
            return this;
        }

        public Builder sequence(Integer sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder validStartAt(LocalDateTime validStartAt) {
            this.validStartAt = validStartAt;
            return this;
        }

        public Builder validEndAt(LocalDateTime validEndAt) {
            this.validEndAt = validEndAt;
            return this;
        }
    }
}
