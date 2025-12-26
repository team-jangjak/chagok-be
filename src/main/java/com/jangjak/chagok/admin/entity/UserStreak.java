package com.jangjak.chagok.admin.entity;

import com.jangjak.chagok.common.enums.YN;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_streak")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserStreak {
    @Id
    private Long userId;

    private Integer streak;

    @Enumerated(EnumType.STRING)
    private YN isFreeze;

    private LocalDate lastSuccessDate;

    private LocalDateTime updatedAt;


    // 스트릭 증가
    public void incrementStreak(LocalDate date) {
        // 마지막 성공 날짜가 오늘이 아닐 때만 증가 (중복 방지)
        if (this.lastSuccessDate == null || !this.lastSuccessDate.isEqual(date)) {
            this.streak++;
            this.lastSuccessDate = date;
            this.isFreeze = YN.N;
        }
    }

    // 스트릭 초기화
    public void resetStreak() {
        this.streak = 0;
        this.lastSuccessDate = null;
        this.isFreeze = YN.N;
    }

    // freeze 사용
    public void useFreeze() {
        this.isFreeze = YN.N;
    }


    public void updateStreak(int streak, YN isFreeze) {
        this.streak = streak;
        this.isFreeze = isFreeze;
        this.updatedAt = LocalDateTime.now();
    }
}
