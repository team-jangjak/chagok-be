package com.jangjak.chagok.habit.service;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.create.ActionVerifyRequestDto;
import com.jangjak.chagok.habit.dto.request.create.CreateCheckMethodRequestDto;
import com.jangjak.chagok.habit.dto.response.CheckMethodDetailRestDto;
import com.jangjak.chagok.habit.dto.response.CheckMethodResDto;
import com.jangjak.chagok.habit.entity.*;
import com.jangjak.chagok.habit.repository.ActionVerifyRepository;
import com.jangjak.chagok.habit.repository.CheckMethodDetailRepository;
import com.jangjak.chagok.habit.repository.CheckMethodRepository;
import com.jangjak.chagok.habit.repository.HabitQuery;
import com.jangjak.chagok.habit.service.read.HabitReadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckMethodService {

    private final CheckMethodRepository checkMethodRepository;
    private final CheckMethodDetailRepository checkMethodDetailRepository;
    private final ActionVerifyRepository actionVerifyRepository;
    private final HabitQuery habitQuery;


    /**
     * 새로운 인증 템플릿 생성
     *
     * @param id
     * @param requestDto
     * @return
     */
    @Transactional
    public Long createCheckMethod(Long id, CreateCheckMethodRequestDto requestDto) {

        // 입력값 검증
        if (CollectionUtils.isEmpty(requestDto.getDetails())) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // checkMethod: userId, title
        CheckMethod checkMethod = CheckMethod.builder()
                .userId(id)
                .title(requestDto.getTitle())
                .build();

        CheckMethod savedCheckMethod = checkMethodRepository.save(checkMethod);

        // checkMethodDetail: checkMethodId, order, type, value
        List<CheckMethodDetail> detailEntities = requestDto.getDetails().stream()
                .map(detailDto -> CheckMethodDetail.builder()
                        .checkMethodId(savedCheckMethod.getId())
                        .methodOrder(detailDto.getMethodOrder())
                        .type(detailDto.getType())
                        .value(detailDto.getValue())
                        .build())
                .toList();

        checkMethodDetailRepository.saveAll(detailEntities);

        return savedCheckMethod.getId();
    }

    /**
     * 인증 템플릿 전체 수정
     */
    @Transactional
    public Long modifyCheckMethod(Long id, Long checkMethodId, @Valid CreateCheckMethodRequestDto requestDto) {

        // 입력값 검증
        if (CollectionUtils.isEmpty(requestDto.getDetails())) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // 기존 템플릿 조회
        CheckMethod checkMethod = checkMethodRepository.findById(checkMethodId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 권한 체크 (userId 비교)
        if (!Objects.equals(checkMethod.getUserId(), id)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 템플릿 기본 정보 수정 (title)
        checkMethod.updateTitle(requestDto.getTitle());

        // 기존 detail 삭제
        checkMethodDetailRepository.deleteAllByCheckMethodId(checkMethodId);

        // 새 detail 생성
        List<CheckMethodDetail> newDetails = requestDto.getDetails().stream()
                .map(dto -> CheckMethodDetail.builder()
                        .checkMethodId(checkMethodId)
                        .methodOrder(dto.getMethodOrder())
                        .type(dto.getType())
                        .value(dto.getValue())
                        .build())
                .toList();

        checkMethodDetailRepository.saveAll(newDetails);

        return checkMethodId;
    }

    /**
     * action 인증
     */
    public Long actionVerify(Long id, Long userActionId, ActionVerifyRequestDto requestDto) {

        // userId와 비교 검증
        UserAction userActionById = habitQuery.getUserActionById(userActionId);
        UserHabit userHabit = habitQuery.getUserHabit(userActionById.getUserHabitId());
        if (!userHabit.getUserId().equals(id)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        ActionVerify verify = ActionVerify.builder()
                .id(userActionId)
                .checkMethodId(requestDto.getCheckMethodId())
                .verifyDate(requestDto.getVerifyDate())
                .value(requestDto.getValue())
                .build();

        actionVerifyRepository.save(verify);

        return verify.getId();
    }

    /**
     * Action 인증을 위한 인증 방식(check method) 조회
     *
     * @return
     */
    public CheckMethodResDto checkMethodOfAction(Long id, Long actionId) {
        Action actionById = habitQuery.findActionById(actionId);

        CheckMethod checkMethod = checkMethodRepository.findById(actionById.getCheckMethodId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND));

        List<CheckMethodDetail> details = checkMethodDetailRepository.findByCheckMethodIdOrderByMethodOrderAsc(actionById.getCheckMethodId());

        if (details.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        List<CheckMethodDetailRestDto> detailResDto = details.stream()
                .map(detail -> CheckMethodDetailRestDto.builder()
                        .type(detail.getType())
                        .value(detail.getValue())
                        .build()
                ).toList();

        return CheckMethodResDto.builder()
                .id(actionById.getCheckMethodId())
                .title(checkMethod.getTitle())
                .details(detailResDto)
                .build();
    }


}
