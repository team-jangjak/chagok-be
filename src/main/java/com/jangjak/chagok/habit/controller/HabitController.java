package com.jangjak.chagok.habit.controller;

import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.habit.dto.request.CustomHabitRequestDto;
import com.jangjak.chagok.habit.service.creation.HabitCreateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/habit")
@RequiredArgsConstructor
@Slf4j
public class HabitController {
    private final HabitCreateService habitCreateService;

    @PostMapping("/custom")
    public ResponseEntity<?> createCustomHabit(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody CustomHabitRequestDto reqDto) {
        Long userHabitId = habitCreateService.createCustomHabit(reqDto, userInfo.getId());
        return CommonResponse.toRes(userHabitId,"습관 생성이 완료되었습니다.");
    }
}
