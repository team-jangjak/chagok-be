package com.jangjak.chagok.habit.controller;

import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.habit.controller.docs.HabitControllerDocs;
import com.jangjak.chagok.habit.dto.request.create2.HabitCreateRequestDto;
import com.jangjak.chagok.habit.dto.request.delete.HabitDeleteRequestDto;
import com.jangjak.chagok.habit.dto.request.update.HabitUpdateRequestDto;
import com.jangjak.chagok.habit.dto.response.CalendarViewResDto;
import com.jangjak.chagok.habit.dto.response.HabitDashboardResDto;
import com.jangjak.chagok.habit.dto.response.HabitDetailResDto;
import com.jangjak.chagok.habit.enums.HabitState;
import com.jangjak.chagok.habit.service.create.HabitCreateService;
import com.jangjak.chagok.habit.service.delete.HabitDeleteService;
import com.jangjak.chagok.habit.service.read.HabitReadService;
import com.jangjak.chagok.habit.service.update.HabitUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/habit")
@RequiredArgsConstructor
@Slf4j
public class HabitController implements HabitControllerDocs {
    private final HabitCreateService habitCreateService;
    private final HabitReadService habitReadService;
    private final HabitUpdateService habitUpdateService;
    private final HabitDeleteService habitDeleteService;

    /**
     * 사용자 습관 (userHabit) 생성
     * 습관은 독립적으로 생성할 수 없고, 사용자 습관을 생성하면서 함께 생성하는 방법밖에 없음.
     * @param userInfo 토큰에서 받아온 값
     * @param reqDto 습관 생성 공통 DTO
     * @return userHabitId
     */
    @PostMapping()
    public ResponseEntity<?> createHabit(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Valid @RequestBody HabitCreateRequestDto reqDto
    ) {
        Long userHabitId = habitCreateService.createHabit(userInfo.getId(), reqDto);
        return CommonResponse.toRes(userHabitId, "습관이 성공적으로 생성되었습니다.");
    }

    /**
     * 습관 수정
     * 사용자 습관, 사용자 행위에 영향 X
     * 순수한 습관과 행위만 수정 가능
     * @param userInfo 토큰에서 받아온 값
     * @param reqDto 습관 수정 DTO
     * @return habitId
     */
    @PatchMapping()
    public ResponseEntity<?> updateHabit(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Valid @RequestBody HabitUpdateRequestDto reqDto
    ) {
        Long habitId = habitUpdateService.updateHabit(userInfo.getId(), reqDto);
        return CommonResponse.toRes(habitId, "습관이 성공적으로 수정되었습니다.");
    }

    /**
     * 습관 삭제
     * 영구 삭제가 아닌 유효기간 만료를 통한 삭제
     * @param userInfo 토큰에서 받아온 값
     * @param reqDto 습관 삭제 DTO
     * @return habitId
     */
    @DeleteMapping()
    public ResponseEntity<?> deleteHabit(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Valid @RequestBody HabitDeleteRequestDto reqDto
    ) {
       habitDeleteService.deleteHabit(userInfo.getId(), reqDto);
       return CommonResponse.toRes(reqDto.getId(), "습관이 성공적으로 삭제되었습니다.");
    }

    /**
     * 인기 습관 카테고리 정보 조회
     */
//    @GetMapping("/popular-habit-category")
//    public ResponseEntity<?> getPopularHabitCategory() {
//        return CommonResponse.toRes(habitReadService.getPopularHabitCategory(), "인기 습관 카테고리가 조회되었습니다.");
//    }

    /**
     * 습관 대시보드 조회
     */
    @GetMapping("/habit-dashboard")
    public ResponseEntity<?> getHabitDashboard(@AuthenticationPrincipal TokenUserInfo userInfo) {
        List<HabitDashboardResDto> habitDashboard = habitReadService.getHabitDashboard(userInfo.getId());
        return CommonResponse.toRes(habitDashboard,"습관 대시보드가 조회되었습니다.");
    }

    /**
     * 캘린더 뷰
     */
    @GetMapping("/calendar-view")
    public ResponseEntity<?> getCalendarView(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestParam Integer year, @RequestParam Integer month,
                                             @RequestParam(required = false) Long userHabitId, @RequestParam(required = false) HabitState state) {
        List<CalendarViewResDto> calendarView = habitReadService.getCalendarView(userInfo.getId(), year, month, userHabitId, state);
        return CommonResponse.toRes(calendarView,"캘린더가 조회되었습니다.");
    }

    /**
     * 습관&액션 상세 조회
     */
    @GetMapping("/habit-detail/{userActionId}")
    public ResponseEntity<?> getHabitDetail(@AuthenticationPrincipal TokenUserInfo userInfo, @PathVariable Long userActionId) {
        HabitDetailResDto actionDetail = habitReadService.getHabitDetail(userInfo.getId(), userActionId);
        return CommonResponse.toRes(actionDetail,"습관 상세 정보가 조회되었습니다.");
    }


}
