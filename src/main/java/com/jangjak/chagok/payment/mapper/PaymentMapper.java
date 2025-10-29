package com.jangjak.chagok.payment.mapper;

import com.jangjak.chagok.payment.entity.Payment;
import com.jangjak.chagok.payment.enums.OrderState;

public class PaymentMapper {
    public static Payment toEntity(Long userHabitId, OrderState orderState) {
        return Payment.builder()
                .userHabitId(userHabitId)
                .orderState(OrderState.CREATED)
                .build();
    }
}
