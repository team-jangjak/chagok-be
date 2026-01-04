package com.jangjak.chagok.habit.service;

import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.create.ActionVerifyRequestDto;
import com.jangjak.chagok.habit.dto.request.create.CreateCheckMethodRequestDto;
import com.jangjak.chagok.habit.dto.request.update.CheckMethodUpdateRequestDto;
import com.jangjak.chagok.habit.dto.response.CheckMethodDetailRestDto;
import com.jangjak.chagok.habit.dto.response.CheckMethodResDto;
import com.jangjak.chagok.habit.dto.response.VerifyOfActionResDto;
import com.jangjak.chagok.habit.entity.*;
import com.jangjak.chagok.habit.mapper.ActionVerifyMapper;
import com.jangjak.chagok.habit.mapper.CheckMethodDetailMapper;
import com.jangjak.chagok.habit.mapper.CheckMethodMapper;
import com.jangjak.chagok.habit.repository.*;
import com.jangjak.chagok.habit.service.read.HabitReadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckMethodService {

    private final CheckMethodRepository checkMethodRepository;
    private final CheckMethodDetailRepository checkMethodDetailRepository;
    private final ActionVerifyRepository actionVerifyRepository;
    private final HabitQuery habitQuery;
    private final HabitReadService habitReadService;
    private final QueryRepository queryRepository;


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
                .validStartAt(LocalDateTime.now())
                .validEndAt(LocalDateTime.of(9999, 12, 31, 23, 59, 59))
                .build();

        CheckMethod savedCheckMethod = checkMethodRepository.save(checkMethod);

        // checkMethodDetail: checkMethodId, order, type, value
        List<CheckMethodDetail> detailEntities = requestDto.getDetails().stream()
                .map(detailDto -> CheckMethodDetail.builder()
                        .checkMethodId(savedCheckMethod.getId().getCheckMethodId())
                        .methodOrder(detailDto.getMethodOrder())
                        .type(detailDto.getType())
                        .value(detailDto.getValue())
                        .validStartAt(LocalDateTime.now())
                        .validEndAt(LocalDateTime.of(9999, 12, 31, 23, 59, 59))
                        .build())
                .toList();

        checkMethodDetailRepository.saveAll(detailEntities);

        return savedCheckMethod.getId().getCheckMethodId();
    }

    /**
     * 인증 템플릿 수정
     */
    @Transactional
    public Long modifyCheckMethod(Long id, Long checkMethodId, @Valid CheckMethodUpdateRequestDto requestDto) {
        LocalDateTime validStDt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 입력값 검증

        // 기존 템플릿 조회
        CheckMethod checkMethod = queryRepository.findByCheckMethodIdAndCreatedAt(checkMethodId, validStDt).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND));

        // 권한 체크 (userId 비교)
        if (!Objects.equals(checkMethod.getUserId(), id)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 기존 인증방식 만료
        checkMethodRepository.expireCheckMethod(checkMethodId, validStDt, LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        // 업데이트 된 인증방식 저장
        CheckMethod updateCheckMethod = CheckMethodMapper.updateFrom(checkMethod, requestDto, validStDt);
        checkMethodRepository.save(updateCheckMethod);


        // 세부 인증 방식이 수정되지 않았으면 리턴
        if(requestDto.getDetails() == null || requestDto.getDetails().isEmpty()) {
            return checkMethodId;
        }

        // 기존 세부 인증 방식 만료
        List<CheckMethodDetail> details = queryRepository.findDetailsByCheckMethodIdAndCreatedAt(checkMethodId, validStDt);
        checkMethodDetailRepository.expireCheckMethodDetail(details.get(0).getId().getCheckMethodId(), validStDt, LocalDateTime.of(9999, 12, 31, 23, 59, 59));

        // 수정된 세부 사항 저장
        List<CheckMethodDetail> checkMethodDetails = CheckMethodDetailMapper.updateFrom(checkMethodId, requestDto, validStDt);
        checkMethodDetailRepository.saveAll(checkMethodDetails);


        return checkMethodId;
    }

    /**
     * action 인증
     */
    @Transactional
    public Long actionVerify(Long id, Long userActionId, List<ActionVerifyRequestDto> requestDto) {

        validateRequestNotEmpty(requestDto);
        // userId와 비교 검증
        UserAction userAction = habitReadService.validate(id, userActionId);
        validateActionAvailability(userAction);

        // action에서 checkMethodId 가져오기
        Action actionById = habitQuery.findByActionIdAndCreatedAt(userAction.getActionId(), userAction.getCreatedAt());

        // 인증 템플릿 구조 가져오기
        CheckMethodResDto checkMethodResDto = getCheckMethodResDto(actionById.getCheckMethodId(), actionById.getCreatedAt());

        log.info("checkMethodResDto: {}", checkMethodResDto);

        // **확인해봐야함
        List<ActionVerify> verifies = toEntityList(userActionId, actionById.getCheckMethodId(), checkMethodResDto, requestDto);

        actionVerifyRepository.saveAll(verifies);
        userAction.complete();

        return userActionId;
    }

    private void validateRequestNotEmpty(List<ActionVerifyRequestDto> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    private void validateActionAvailability(UserAction userAction) {
        // 오늘 날짜인지 확인
        if (!userAction.getActionDate().isEqual(LocalDate.now())) {
            throw new CustomException(ErrorCode.NOT_ACTION_DATE);
        }
        // 이미 인증되었는지 확인
        if (actionVerifyRepository.existsByUserActionId(userAction.getUserActionId())) {
            throw new CustomException(ErrorCode.ALREADY_VERIFIED);
        }
    }

    private List<ActionVerify> toEntityList(Long userActionId, Long checkMethodId,
                                            CheckMethodResDto checkMethodResDto,
                                            List<ActionVerifyRequestDto> requestDto) {

        List<CheckMethodDetailRestDto> details = checkMethodResDto.getDetails();

        // 개수 검증
        if (details.size() != requestDto.size()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        Set<Long> detailsSet = details.stream()
                .map(CheckMethodDetailRestDto::getMethodOrder)
                .collect(Collectors.toSet());

        return requestDto.stream()
                .map(req -> {
                    if (!detailsSet.contains(req.getMethodOrder().longValue())) {
                        throw new CustomException(ErrorCode.BAD_REQUEST);
                    }
                    return ActionVerifyMapper.toEntity(userActionId, checkMethodId, req);
                })
                .toList();
    }

    /**
     * Action 인증을 위한 인증 방식(check method) 조회
     *
     * @return
     */
    public CheckMethodResDto checkMethodOfAction(Long id, Long userActionId) {
        UserAction userAction = habitQuery.getUserActionById(userActionId);
        Action actionById = habitQuery.findByActionIdAndCreatedAt(userAction.getActionId(), userAction.getCreatedAt());

        return getCheckMethodResDto(actionById.getCheckMethodId(), actionById.getCreatedAt());
    }

    private CheckMethodResDto getCheckMethodResDto(Long checkMethodId, LocalDateTime createdAt) {  // action의 createdAt
        CheckMethod checkMethod = queryRepository.findByCheckMethodIdAndCreatedAt(checkMethodId, createdAt).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND));

        List<CheckMethodDetail> details = queryRepository.findDetailsByCheckMethodIdAndCreatedAt(checkMethodId, checkMethod.getCreatedAt());

        if (details.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        List<CheckMethodDetailRestDto> detailResDto = details.stream()
                .map(detail -> CheckMethodDetailRestDto.builder()
                        .type(detail.getType())
                        .value(detail.getValue())
                        .methodOrder(detail.getId().getMethodOrder())
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
        List<ActionVerify> verifyList = actionVerifyRepository.findAllByUserActionId(userActionId);

        // 인증 방식 구조 가져오기
        Action action = habitQuery.findByActionIdAndCreatedAt(userAction.getActionId(), userAction.getCreatedAt());
        CheckMethodResDto checkMethodResDto = getCheckMethodResDto(action.getCheckMethodId(), action.getCreatedAt());

        log.info("조회된 인증방식: {}", checkMethodResDto);

        Map<Integer, String> verifyMap = verifyList.stream()
                .collect(Collectors.toMap(
                        ActionVerify::getMethodOrder,
                        ActionVerify::getValue
                ));

        List<CheckMethodDetailRestDto> detailRestDtoList =
                checkMethodResDto.getDetails().stream()
                        .sorted(Comparator.comparing(CheckMethodDetailRestDto::getMethodOrder))
                        .map(detailRestDto -> CheckMethodDetailRestDto.builder()
                                .methodOrder(detailRestDto.getMethodOrder())
                                .type(detailRestDto.getType())
                                .value(detailRestDto.getValue()) // 질문
                                .answer(verifyMap.get(detailRestDto.getMethodOrder()))
                                .build()
                        )
                        .toList();


        return VerifyOfActionResDto.builder()
                .content(action.getContent())
                .verifyDate(verifyList.isEmpty() ? null : verifyList.get(0).getVerifyDate())
                .details(detailRestDtoList)
                .build();
    }

    @Transactional
    public void deleteCheckMethod(Long userId, Long checkMethodId) {
        LocalDateTime validStDt = LocalDateTime.now();

        // 인증 방식 조회
        CheckMethod checkMethod = checkMethodRepository.findByCheckMethodId(validStDt, checkMethodId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );

        // 사용자가 생성한 인증 방식이 맞는 지 확인
        if (!checkMethod.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 인증 방식 만료
        checkMethodRepository.expireCheckMethod(checkMethodId, validStDt, LocalDateTime.MAX);

        // 인증 방식 세부 사항 만료
        checkMethodDetailRepository.expireCheckMethodDetail(checkMethodId, validStDt, LocalDateTime.MAX);
    }
}
