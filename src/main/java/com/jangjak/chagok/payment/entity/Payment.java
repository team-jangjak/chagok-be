package com.jangjak.chagok.payment.entity;

import com.jangjak.chagok.common.dto.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Payment extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userHabitId;

    private Integer price;

    // Enum으로 관리
    private String payMethod;

    private String paymentKey;

    private String orderId;

    // Enum으로 관리
    private String orderState;
}
