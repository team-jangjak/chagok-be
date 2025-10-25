package com.jangjak.chagok.habit.util;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.CustomHabitRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitDateInfo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidationUtil {
    public static boolean validateAndParseHabitDates(LocalDate start, LocalDate end) {
        // 논리적 유효성 검증: 시작일이 종료일보다 늦으면 안됨
        if (start.isAfter(end)) {
            return false;
        }

        // TODO: 추가 검증 필요
        // - 시작일이 과거 날짜인지 확인
        // - 최대 습관 기간 제한 (예: 1년)
        // - 공휴일/주말 제외 옵션 등

        return true;
    }
}
