package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.external.service.s3.S3ImageService;
import com.jangjak.chagok.habit.dto.request.create.*;
import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.*;
import com.jangjak.chagok.habit.enums.HabitCategory;
import com.jangjak.chagok.habit.mapper.ActionMapper;
import com.jangjak.chagok.habit.mapper.HabitMapper;
import com.jangjak.chagok.habit.mapper.UserActionMapper;
import com.jangjak.chagok.habit.mapper.UserHabitMapper;
import com.jangjak.chagok.habit.repository.HabitQuery;
import com.jangjak.chagok.habit.util.DateValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewHabitCreation implements HabitCreation {
    private final HabitQuery habitQuery;
    private final S3ImageService s3ImageService;

    @Override
    public boolean validateRequest(Long userId, CreateHabitRequestDto reqDto) {
        NewHabitRequestDto request = convertRequest(reqDto);

        // 날짜 검증
        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();
        if (!DateValidationUtil.validateAndParseHabitDates(start, end)) return false;

        // action 검증
        List<NewActionRequestDto> actions = request.getActions();
        if (actions == null || actions.isEmpty()) return false;


        // 인증방식 검증
        List<Long> checkMethodIdList = actions.stream()
                .map(NewActionRequestDto::getCheckMethodId)
                .distinct().toList();

        if(!habitQuery.isUserCheckMethod(userId, checkMethodIdList)) return false;

        // 카테고리 검증
        HabitCategory habitCategory = HabitCategory.fromValue(request.getCategory().intValue());
        if (habitCategory == HabitCategory.NONE) return false;
//        if (!habitQuery.getHabitCategoryById(request.getCategoryId())) return false;

        // frequency 검증
        int frequency = request.getFrequency();
        int freqUnit = request.getFreqUnit();

        switch (freqUnit) {
            case 1:
                // TODO Habit FreqUnit에 따른 Frequency 기준
                break;
            case 2:
                if (frequency > 7 || frequency < 1) return false;
            case 3:
                // TODO Habit FreqUnit에 따른 Frequency 기준
                if (frequency > 28 || frequency < 1) return false;
        }

        return true;
    }

    @Override
    public boolean validateRequest(Long userId, HabitCreateRequestDto reqDto) {
        // 카테고리 검증
        HabitCategory habitCategory = HabitCategory.fromValue(reqDto.getHabitCategory().intValue());
        if (habitCategory == HabitCategory.NONE) return false;

        return true;
    }

    @Override
    public HabitCreationInfo createHabit(HabitCreateRequestDto reqDto, LocalDateTime validStDt) {
        // 이미지 등록
        String imageUrl = s3ImageService.registerImage(reqDto.getHabitImage());
        reqDto.setHabitImage(imageUrl);

        // 습관 생성
        Habit habit = HabitMapper.toEntity(reqDto, validStDt);
        Long habitId = habitQuery.saveHabit(habit).getId().getHabitId();

        // 행위 생성
        List<Action> actions = ActionMapper.toEntities(habitId, reqDto, validStDt);
        habitQuery.saveActionList(actions);

        return new HabitCreationInfo(habitId, actions);
    }

    private NewHabitRequestDto convertRequest(CreateHabitRequestDto reqDto) {
        if (!(reqDto instanceof NewHabitRequestDto)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return (NewHabitRequestDto) reqDto;
    }
}
