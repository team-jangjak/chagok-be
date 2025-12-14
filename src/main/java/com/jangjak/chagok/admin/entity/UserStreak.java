package com.jangjak.chagok.admin.entity;

import com.jangjak.chagok.common.enums.YN;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private LocalDateTime updatedAt;

    public static UserStreak newUser(Long userId) {
        UserStreak us = new UserStreak();
        us.userId = userId;
        us.streak = 0;
        us.isFreeze = YN.N;
        us.updatedAt = LocalDateTime.now();
        return us;
    }

    public void updateStreak(int streakDays, YN isFreeze) {
        this.streak = streakDays;
        this.isFreeze = isFreeze;
        this.updatedAt = LocalDateTime.now();
    }
}
