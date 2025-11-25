package com.jangjak.chagok.habit.controller;

import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.habit.controller.docs.HabitControllerDocs;
import com.jangjak.chagok.habit.dto.request.create.ModifyHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.NewHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.TemplateHabitRequestDto;
import com.jangjak.chagok.habit.dto.response.HabitDashboardResDto;
import com.jangjak.chagok.habit.service.creation.HabitCreateService;
import com.jangjak.chagok.habit.service.read.HabitReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habit")
@RequiredArgsConstructor
@Slf4j
public class HabitController implements HabitControllerDocs {
    private final HabitCreateService habitCreateService;
    private final HabitReadService habitReadService;

    @PostMapping("/new")
    public ResponseEntity<?> createNewHabit(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody NewHabitRequestDto reqDto) {
        Long userHabitId = habitCreateService.createNewHabit(userInfo, reqDto);
        return CommonResponse.toRes(userHabitId, "습관 생성이 완료되었습니다.");
    }

    @PostMapping("/modify")
    public ResponseEntity<?> createModifyHabit(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody ModifyHabitRequestDto reqDto) {
        Long userHabitId = habitCreateService.createModifyHabit(userInfo, reqDto);
        return CommonResponse.toRes(userHabitId, "습관 생성이 완료되었습니다.");
    }

    @PostMapping("/template")
    public ResponseEntity<?> createTemplateHabit(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody TemplateHabitRequestDto reqDto) {
        Long userHabitId = habitCreateService.createTemplateHabit(userInfo, reqDto);
        return CommonResponse.toRes(userHabitId, "습관 생성이 완료되었습니다.");
    }

    @PostMapping("/test")
    public ResponseEntity<?> testHabit(@RequestBody ModifyHabitRequestDto reqDto) {
        TokenUserInfo userInfo = new TokenUserInfo(2L, "USER");
        Long userHabitId = habitCreateService.createModifyHabit(userInfo, reqDto);
        return CommonResponse.toRes(userHabitId, "습관 생성이 완료되었습니다.");
    }

    /**
     * 인기 습관 카테고리 정보 조회
     */
//    @GetMapping("/popular-habit-category")
//    public ResponseEntity<?> getPopularHabitCategory() {
//        return CommonResponse.toRes(habitReadService.getPopularHabitCategory(), "인기 습관 카테고리가 조회되었습니다.");
//    }

    @GetMapping("/habit-dashboard")
    public ResponseEntity<?> getHabitDashboard(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<HabitDashboardResDto> habitDashboard = habitReadService.getHabitDashboard(userInfo.getId());
        return CommonResponse.toRes(habitDashboard,"습관 대시보드가 조회되었습니다.");
    }
}
