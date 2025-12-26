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

    public static CheckMethodDetail.Build builder() {
        return new CheckMethodDetail.Build();
    }

    public static class Build {
        private Long checkMethodId = null;
        private CheckMethodType type;
        private String value;
        private Long methodOrder;
        private LocalDateTime validStartAt;
        private LocalDateTime validEndAt;

        public CheckMethodDetail build() {
            return new CheckMethodDetail(
                    new CheckMethodDetailCompositeKey(checkMethodId, methodOrder, validEndAt),
                    type,
                    value,
                    validStartAt
            );
        }

        public Build type(CheckMethodType type) {
            this.type = type;
            return this;
        }

        public Build value(String value) {
            this.value = value;
            return this;
        }

        public Build methodOrder(Long methodOrder) {
            this.methodOrder = methodOrder;
            return this;
        }

        public Build validStartAt(LocalDateTime validStartAt) {
            this.validStartAt = validStartAt;
            return this;
        }

        public Build validEndAt(LocalDateTime validEndAt) {
            this.validEndAt = validEndAt;
            return this;
        }
    }
}
