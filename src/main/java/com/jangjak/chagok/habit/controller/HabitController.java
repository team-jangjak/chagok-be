package com.jangjak.chagok.habit.controller;

import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.habit.controller.docs.HabitControllerDocs;
import com.jangjak.chagok.habit.dto.request.CustomHabitRequestDto;
import com.jangjak.chagok.habit.service.creation.HabitCreateService;
import com.jangjak.chagok.habit.service.read.HabitReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/habit")
@RequiredArgsConstructor
@Slf4j
public class HabitController implements HabitControllerDocs {
    private final HabitCreateService habitCreateService;
    private final HabitReadService habitReadService;

    @PostMapping("/custom")
    public ResponseEntity<?> createCustomHabit(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody CustomHabitRequestDto reqDto) {
        Long userHabitId = habitCreateService.createCustomHabit(reqDto, userInfo.getId());
        return CommonResponse.toRes(userHabitId,"습관 생성이 완료되었습니다.");
    }

    /**
     * 인기 습관 카테고리 정보 조회
     */
    @GetMapping("/popular-habit-category")
    public ResponseEntity<?> getPopularHabitCategory() {
        return CommonResponse.toRes(habitReadService.getPopularHabitCategory(),"인기 습관 카테고리가 조회되었습니다.");
    }

    @GetMapping("/habit-dashboard")
    public ResponseEntity<?> getHabitDashboard(@AuthenticationPrincipal TokenUserInfo userInfo) {
        habitReadService.getHabitDashboard(userInfo.getId());
        return CommonResponse.toRes(null,"습관 대시보드가 조회되었습니다.");
    }
}
