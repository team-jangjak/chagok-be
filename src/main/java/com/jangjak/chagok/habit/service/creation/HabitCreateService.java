package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.create.CreateHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.ModifyHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.NewHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.TemplateHabitRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitDateInfo;
import com.jangjak.chagok.habit.dto.request.CustomHabitRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.entity.UserAction;
import com.jangjak.chagok.habit.entity.UserHabit;
import com.jangjak.chagok.habit.enums.HabitCreationType;
import com.jangjak.chagok.habit.repository.HabitQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;

/**
 * 습관 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 
 * 주요 기능:
 * - 사용자 정의 습관 생성 및 기존 습관 재활용
 * - 습관과 연관된 액션들의 사용자별 상태 관리
 * - Factory 패턴을 통한 습관 생성 전략 분리
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HabitCreateService {
    /** 습관 생성 전략을 결정하는 팩토리 클래스 */
    private final HabitCreationFactory creationFactory;
    private final HabitCreationTxService creationTxService;

    public Long createNewHabit(TokenUserInfo userInfo, NewHabitRequestDto reqDto) {
        HabitCreation manager = creationFactory.getHabitCreation(HabitCreationType.NEW);
        return creationTxService.createHabit(manager, userInfo, reqDto);
    }

    public Long createModifyHabit(TokenUserInfo userInfo, ModifyHabitRequestDto reqDto) {
        HabitCreation manager = creationFactory.getHabitCreation(HabitCreationType.MODIFY);
        return creationTxService.createHabit(manager, userInfo, reqDto);
    }

    public Long createTemplateHabit(TokenUserInfo userInfo, TemplateHabitRequestDto reqDto) {
        HabitCreation manager = creationFactory.getHabitCreation(HabitCreationType.TEMPLATE);
        return creationTxService.createHabit(manager, userInfo, reqDto);
    }
}
