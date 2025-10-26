package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.create.CreateHabitRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.UserHabit;
import com.jangjak.chagok.habit.repository.HabitQuery;
import com.jangjak.chagok.payment.entity.Payment;
import com.jangjak.chagok.payment.enums.OrderState;
import com.jangjak.chagok.payment.mapper.PaymentMapper;
import com.jangjak.chagok.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HabitCreationTxService {
    private final PaymentRepository paymentRepository;

    @Transactional
    Long createHabit(HabitCreation manager, TokenUserInfo userInfo, CreateHabitRequestDto reqDto) {
        Long userId = userInfo.getId();

        // 입력값 검증
        if (!manager.validateRequest(userId, reqDto)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // 습관 및 액션 생성
        HabitCreationInfo habitInfo = manager.createHabit(reqDto);

        // 사용자 습관 및 액션 생성
        Long userHabitId = manager.createUserHabit(userId, reqDto, habitInfo);

        // 결제 생성
        Payment payment = PaymentMapper.toEntity(userHabitId, OrderState.CREATED);
        paymentRepository.save(payment);

        return userHabitId;
    }
}
