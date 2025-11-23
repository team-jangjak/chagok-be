package com.jangjak.chagok.habit.entity;

import com.jangjak.chagok.common.dto.BaseTimeEntity;
import com.jangjak.chagok.common.enums.YN;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
public class UserAction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userHabitId;

    private Long actionId;

    private LocalDate actionDate;

    @Enumerated(EnumType.STRING)
    private YN isCompleted;

    private Integer delayCount;

    public void complete() {
        this.isCompleted = YN.Y;
    }

}
