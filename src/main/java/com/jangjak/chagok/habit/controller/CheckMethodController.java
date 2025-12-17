package com.jangjak.chagok.habit.controller;

import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.habit.dto.request.create.ActionVerifyRequestDto;
import com.jangjak.chagok.habit.dto.request.create.CreateCheckMethodRequestDto;
import com.jangjak.chagok.habit.dto.response.CheckMethodResDto;
import com.jangjak.chagok.habit.dto.response.VerifyOfActionResDto;
import com.jangjak.chagok.habit.service.CheckMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/verify")
@RequiredArgsConstructor
@Slf4j
public class CheckMethodController {

    private final CheckMethodService checkMethodService;

    /**
     * 인증 방식(check method) 생성
     */
    @PostMapping("/new")
    public ResponseEntity<?> createNewHabit(@AuthenticationPrincipal TokenUserInfo userInfo, @Valid @RequestBody CreateCheckMethodRequestDto requestDto) {
        Long checkMethodId = checkMethodService.createCheckMethod(userInfo.getId(), requestDto);
        return CommonResponse.toRes(checkMethodId, "인증 템플릿 생성이 완료되었습니다.");
    }

    /**
     * 인증 방식(check method) 수정
     */
    @PutMapping("/modify/{checkMethodId}")
    public ResponseEntity<?> modifyCheckMethod(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @PathVariable Long checkMethodId, @Valid @RequestBody CreateCheckMethodRequestDto requestDto) {
        Long updatedId = checkMethodService.modifyCheckMethod(userInfo.getId(), checkMethodId, requestDto);
        return CommonResponse.toRes(updatedId, "인증 템플릿 수정이 완료되었습니다.");
    }


    /**
     * Action 인증을 위한 인증 방식(check method) 조회
     */
    @GetMapping("/{userActionId}")
    public ResponseEntity<?> getCheckMethodOfAction(@AuthenticationPrincipal TokenUserInfo userInfo, @PathVariable Long userActionId) {
        CheckMethodResDto checkMethodResDto = checkMethodService.checkMethodOfAction(userInfo.getId(), userActionId);
        return CommonResponse.toRes(checkMethodResDto, "인증 방식이 조회되었습니다.");
    }

    /**
     * Action 인증
     * @return {
     *     id : userActionId (기존 verifyId가 사라져 userActionId 반환 추후 반환값 논의 필요)
     * }
     */
    @PostMapping("/{userActionId}")
    public ResponseEntity<?> actionVerify(@AuthenticationPrincipal TokenUserInfo userInfo,
                                          @PathVariable Long userActionId, @RequestBody List<@Valid ActionVerifyRequestDto> reqDto) {
        Long id = checkMethodService.actionVerify(userInfo.getId(), userActionId, reqDto);
        return CommonResponse.toRes(id, "인증이 완료되었습니다.");
    }

    /**
     * Action 인증 내역 조회
     */
    @GetMapping("/details/{userActionId}")
    public ResponseEntity<?> getVerifyOfAction(@AuthenticationPrincipal TokenUserInfo userInfo, @PathVariable Long userActionId) {
        VerifyOfActionResDto verifyOfAction = checkMethodService.getVerifyOfAction(userInfo.getId(), userActionId);
        return CommonResponse.toRes(verifyOfAction, "인증 내역이 조회되었습니다.");
    }
}
