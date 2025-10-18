package com.jangjak.chagok.habit.entity;

import com.jangjak.chagok.habit.enums.CheckMethodType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckMethodDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long checkMethodId;

    private Integer order;

    @Enumerated(EnumType.STRING)
    private CheckMethodType type;

    // 추후 LOB 자료형 고려
    private String value;
}
