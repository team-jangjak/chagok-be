package com.jangjak.chagok.habit.entity;

import com.jangjak.chagok.habit.entity.keys.CheckMethodDetailCompositeKey;
import com.jangjak.chagok.habit.enums.CheckMethodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long checkMethodId;
        private CheckMethodType type;
        private String value;
        private Long methodOrder;
        private LocalDateTime validStartAt;
        private LocalDateTime validEndAt = LocalDateTime.MAX;

        public CheckMethodDetail build() {
            return new CheckMethodDetail(
                    new CheckMethodDetailCompositeKey(checkMethodId, methodOrder, validEndAt),
                    type,
                    value,
                    validStartAt
            );
        }

        public Builder checkMethodId(Long checkMethodId) {
            this.checkMethodId = checkMethodId;
            return this;
        }

        public Builder type(CheckMethodType type) {
            this.type = type;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder methodOrder(Long methodOrder) {
            this.methodOrder = methodOrder;
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
