package com.jangjak.chagok.habit.controller;

import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.habit.dto.request.HabitForActionReqDto;
import com.jangjak.chagok.habit.dto.response.ActionDto;
import com.jangjak.chagok.habit.service.ai.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class OpenAIController {

    private final OpenAIService openAIService;

    @PostMapping("/request")
    public ResponseEntity<?> actionSuggestRequest(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody HabitForActionReqDto request) {
        ActionDto actionDto = openAIService.actionSuggest(userInfo, request);
        return CommonResponse.toRes(actionDto, "계획이 생성되었습니다.");
    }
}
