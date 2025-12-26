package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.create.CreateHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.UserHabit;
import com.jangjak.chagok.habit.repository.HabitQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class HabitCreationTxService {
//    @Transactional
//    Long createHabit(HabitCreation manager, TokenUserInfo userInfo, CreateHabitRequestDto reqDto) {
//        Long userId = userInfo.getId();
//
//        // 입력값 검증
//        if (!manager.validateRequest(userId, reqDto)) {
//            throw new CustomException(ErrorCode.BAD_REQUEST);
//        }
//
//        // 습관 및 액션 생성
//        HabitCreationInfo habitInfo = manager.createHabit(reqDto);
//
//        // 사용자 습관 및 액션 생성
//        Long userHabitId = manager.createUserHabit(userId, reqDto, habitInfo);
//
//        return userHabitId;
//    }

    @Transactional
    Long createHabit(HabitCreation manager, Long userId, HabitCreateRequestDto reqDto) {
        LocalDateTime validStDt = LocalDateTime.now();

        // 입력값 검증
//        manager.validateRequest(userId, reqDto);

        // (Temp Image) to (Real Image)


        // 습관/행위 생성
        manager.createHabit(reqDto, validStDt);

        // 사용자 습관/사용자 행위 생성



//        if (!manager.validateRequest(userId, reqDto)) {
//            throw new CustomException(ErrorCode.BAD_REQUEST);
//        }
//
//         습관 및 액션 생성
//        HabitCreationInfo habitInfo = manager.createHabit(reqDto);
//
//         사용자 습관 및 액션 생성
//        Long userHabitId = manager.createUserHabit(userId, reqDto, habitInfo);
//
//        return userHabitId;
        return 0L;
    }
}
