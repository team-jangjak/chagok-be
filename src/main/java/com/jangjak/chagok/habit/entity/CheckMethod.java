package com.jangjak.chagok.habit.entity;

import com.jangjak.chagok.common.dto.BaseTimeEntity;
import com.jangjak.chagok.habit.entity.keys.CheckMethodCompositeKey;
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
public class CheckMethod extends BaseTimeEntity {
    @EmbeddedId
    private CheckMethodCompositeKey id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime validStartAt;

    public void updateTitle(String title) {
        this.title = title;
    }

    public static CheckMethod.Builder builder() {
        return new CheckMethod.Builder();
    }

    public static class Builder {
        private Long checkMethodId = null;
        private Long userId;
        private String title;
        private LocalDateTime validStartAt;
        private LocalDateTime validEndAt;

        public CheckMethod build() {
            return new CheckMethod(
                    new CheckMethodCompositeKey(checkMethodId, validEndAt),
                    userId,
                    title,
                    validStartAt
            );
        }

        public Builder checkMethodId(Long checkMethodId) {
            this.checkMethodId = checkMethodId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
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
