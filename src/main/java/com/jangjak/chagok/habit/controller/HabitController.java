package com.jangjak.chagok.habit.controller;

import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.habit.controller.docs.HabitControllerDocs;
import com.jangjak.chagok.habit.dto.request.CustomHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.ModifyHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.NewHabitRequestDto;
import com.jangjak.chagok.habit.dto.request.create.TemplateHabitRequestDto;
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

    @PostMapping()
    public ResponseEntity<?> createCustomHabit(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody CustomHabitRequestDto reqDto) {
//        Long userHabitId = habitCreateService.createCustomHabit(reqDto, userInfo.getId());
//        return CommonResponse.toRes(userHabitId, "습관 생성이 완료되었습니다.");
        return CommonResponse.toRes("", "습관 생성이 완료되었습니다.");
    }

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

    /**
     * 인기 습관 카테고리 정보 조회
     */
    @GetMapping("/popular-habit-category")
    public ResponseEntity<?> getPopularHabitCategory() {
        return CommonResponse.toRes(habitReadService.getPopularHabitCategory(), "인기 습관 카테고리가 조회되었습니다.");
    }
}
