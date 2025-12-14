package com.jangjak.chagok.habit.entity;

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
public class ActionVerify {
    @Id
    private Long actionVerifyId;

    @Column(nullable = false)
    private Long checkMethodId;

    @Column(nullable = false)
    private LocalDateTime verifyDate;

    // 얘는 진짜 LOB 자료형 해야될수도
    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private Integer methodOrder;

    // 식별 관계 설정
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_action_id")
    private UserAction userAction;

    public void assignUserAction(UserAction userAction) {
        this.userAction = userAction;
    }
}
