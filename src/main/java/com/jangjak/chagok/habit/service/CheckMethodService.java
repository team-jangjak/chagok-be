package com.jangjak.chagok.habit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.create.ActionVerifyRequestDto;
import com.jangjak.chagok.habit.dto.request.create.CreateCheckMethodRequestDto;
import com.jangjak.chagok.habit.dto.response.CheckMethodDetailRestDto;
import com.jangjak.chagok.habit.dto.response.CheckMethodResDto;
import com.jangjak.chagok.habit.dto.response.VerifyOfActionResDto;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckMethodService {

    private final CheckMethodRepository checkMethodRepository;
    private final CheckMethodDetailRepository checkMethodDetailRepository;
    private final ActionVerifyRepository actionVerifyRepository;
    private final HabitQuery habitQuery;
    private final HabitReadService habitReadService;

    private final ObjectMapper objectMapper;


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

        // 사용되었는지 검증
        if(habitQuery.existsByCheckMethodId(checkMethodId)){
            throw new CustomException(ErrorCode.BAD_REQUEST);
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
    @Transactional
    public Long actionVerify(Long id, Long userActionId, List<ActionVerifyRequestDto> requestDto) {

        // userId와 비교 검증
        UserAction userAction = habitReadService.validate(id, userActionId);

        // actionDate 검증
        if (!userAction.getActionDate().isEqual(LocalDate.now())) {
            throw new CustomException(ErrorCode.NOT_ACTION_DATE);
        }

        // 이미 인증된 데이터라면 에러
        if (userAction.getActionVerify() != null) {
            throw new CustomException(ErrorCode.ALREADY_VERIFIED);
        }

        // action에서 checkMethodId 가져오기
        Long checkMethodId = habitQuery.findActionById(userAction.getActionId()).getCheckMethodId();


        // 인증 템플릿 구조 가져오기
        CheckMethodResDto checkMethodResDto = getCheckMethodResDto(checkMethodId);

        log.info("checkMethodResDto: {}", checkMethodResDto);

        Map<Long, String> answerMap = new LinkedHashMap<>();

        // detail을 methodOrder 기준으로 정렬
        List<CheckMethodDetailRestDto> sortedDetails = checkMethodResDto.getDetails().stream()
                .sorted(Comparator.comparing(CheckMethodDetailRestDto::getMethodOrder))
                .toList();


        // 질문 수와 답변 수가 다르면 에러
        if (requestDto == null || requestDto.isEmpty() || requestDto.size() != sortedDetails.size()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // 매칭
        for (int i = 0; i < sortedDetails.size(); i++) {
            Long order = sortedDetails.get(i).getMethodOrder();
            String answer = requestDto.get(i).getAnswer();
            log.info("order: {}, answer: {}", order, answer);
            answerMap.put(order, answer);
        }

        log.info("answerMap: {}", answerMap);

        String answerJson = "";
        // JSON으로 변환
        try {
            answerJson = objectMapper.writeValueAsString(answerMap);
        } catch (JsonProcessingException e) {
            log.error("JSON 변환 오류: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        ActionVerify verify = ActionVerify.builder()
                .checkMethodId(checkMethodId)
                .verifyDate(LocalDateTime.now())
                .value(answerJson)
                .build();

        // 연관관계 세팅
        verify.assignUserAction(userAction);

        actionVerifyRepository.save(verify);
        userAction.complete();

        return verify.getId();
    }

    /**
     * Action 인증을 위한 인증 방식(check method) 조회
     *
     * @return
     */
    public CheckMethodResDto checkMethodOfAction(Long id, Long userActionId) {
        UserAction userAction = habitQuery.getUserActionById(userActionId);
        Action actionById = habitQuery.findActionById(userAction.getActionId());

        return getCheckMethodResDto(actionById.getCheckMethodId());
    }

    private CheckMethodResDto getCheckMethodResDto(Long checkMethodId) {
        CheckMethod checkMethod = checkMethodRepository.findById(checkMethodId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND));

        List<CheckMethodDetail> details = checkMethodDetailRepository.findByCheckMethodIdOrderByMethodOrderAsc(checkMethodId);

        if (details.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        List<CheckMethodDetailRestDto> detailResDto = details.stream()
                .map(detail -> CheckMethodDetailRestDto.builder()
                        .type(detail.getType())
                        .value(detail.getValue())
                        .methodOrder(detail.getMethodOrder())
                        .build()
                ).toList();

        return CheckMethodResDto.builder()
                .id(checkMethodId)
                .title(checkMethod.getTitle())
                .details(detailResDto)
                .build();
    }

    /**
     * action 인증 내역 조회
     *
     * @return
     */
    public VerifyOfActionResDto getVerifyOfAction(Long id, Long userActionId) {
        // action content, 인증 날짜, 상세 내역
        UserAction userAction = habitReadService.validate(id, userActionId);
        ActionVerify actionVerify = actionVerifyRepository.findById(userActionId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 인증 템플릿 구조 가져오기
        Action action = habitQuery.findActionById(userAction.getActionId());
        CheckMethodResDto checkMethodResDto = getCheckMethodResDto(action.getCheckMethodId());

        Map<Long, String> answerMap;
        try {
            answerMap = objectMapper.readValue(
                    actionVerify.getValue(),
                    new TypeReference<Map<Long, String>>() {
                    }
            );
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        List<CheckMethodDetailRestDto> verifyList = checkMethodResDto.getDetails().stream()
                .sorted(Comparator.comparing(CheckMethodDetailRestDto::getMethodOrder))
                .map(d -> CheckMethodDetailRestDto.builder()
                        .methodOrder(d.getMethodOrder())
                        .type(d.getType())
                        .value(d.getValue())
                        .answer(answerMap.get(d.getMethodOrder()))
                        .build()
                )
                .toList();


        return VerifyOfActionResDto.builder()
                .content(action.getContent())
                .verifyDate(actionVerify.getVerifyDate())
                .details(verifyList)
                .build();
    }
}
