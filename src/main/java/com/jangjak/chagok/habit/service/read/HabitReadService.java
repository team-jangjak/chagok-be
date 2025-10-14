package com.jangjak.chagok.habit.service.read;

import com.jangjak.chagok.habit.dto.value.PopularCategoryDto;
import com.jangjak.chagok.habit.repository.HabitQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HabitReadService {

    /**
     * 습관 관련 데이터베이스 조회/저장을 담당하는 쿼리 클래스
     */
    private final HabitQuery habitQuery;


    /**
     * 인기 습관 카테고리 정보 조회
     */
    public List<PopularCategoryDto> getPopularHabitCategory() {
        return habitQuery.getPopularHabitCategory();
    }
}
