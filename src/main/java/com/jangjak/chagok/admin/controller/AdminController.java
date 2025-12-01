package com.jangjak.chagok.admin.controller;

import com.jangjak.chagok.admin.dto.res.GlobalProgressResDto;
import com.jangjak.chagok.admin.dto.res.UserProgressResDto;
import com.jangjak.chagok.admin.service.AdminService;
import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.common.dto.TokenUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    /**
     * 전체 회원 통계
     */
    @GetMapping("/all-progress")
    public ResponseEntity<?> getAllProgress(@AuthenticationPrincipal TokenUserInfo userInfo) {
        GlobalProgressResDto progress = adminService.getAllProgress(userInfo.getId());
        return CommonResponse.toRes(progress,"전체 회원 통계가 조회되었습니다.");
    }

    /**
     * 각 회원 통계
     */
    @GetMapping("/users-progress")
    public ResponseEntity<?> getUsersProgress(@AuthenticationPrincipal TokenUserInfo userInfo,
                                              @RequestParam int page, @RequestParam int size) {
        Page<UserProgressResDto> usersProgress = adminService.getUsersProgress(userInfo.getId(), page, size);
        return CommonResponse.toRes(usersProgress,"사용자들 통계가 조회되었습니다.");
    }

}
