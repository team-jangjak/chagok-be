package com.jangjak.chagok.habit.service.creation;

import com.jangjak.chagok.common.enums.YN;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.value.HabitDateInfo;
import com.jangjak.chagok.habit.dto.request.CustomHabitRequestDto;
import com.jangjak.chagok.habit.dto.value.HabitCreationInfo;
import com.jangjak.chagok.habit.entity.Action;
import com.jangjak.chagok.habit.entity.UserAction;
import com.jangjak.chagok.habit.entity.UserHabit;
import com.jangjak.chagok.habit.repository.HabitQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;

/**
 * 습관 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 
 * 주요 기능:
 * - 사용자 정의 습관 생성 및 기존 습관 재활용
 * - 습관과 연관된 액션들의 사용자별 상태 관리
 * - Factory 패턴을 통한 습관 생성 전략 분리
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HabitCreateService {
    /** 습관 관련 데이터베이스 조회/저장을 담당하는 쿼리 클래스 */
    private final HabitQuery habitQuery;
    
    /** 습관 생성 전략을 결정하는 팩토리 클래스 */
    private final HabitCreationFactory creationFactory;

    /**
     * 사용자 정의 습관을 생성하거나 기존 습관을 재활용합니다.
     * <p>
     * 이 메서드는 두 가지 시나리오를 처리합니다:
     * <ul>
     *     <li>habitId가 제공된 경우: 기존 습관을 재활용합니다</li>
     *     <li>habitId가 제공되지 않은 경우: 제공된 정보를 바탕으로 새로운 습관을 생성합니다</li>
     * </ul>
     * 참고: 입력 유효성 검증은 @Valid 어노테이션 대신 직접 구현되었습니다.
     *
     * @param reqDto 사용자 정의 습관 생성 요청 데이터
     * @param userId 사용자 Id
     */
    public Long createCustomHabit(CustomHabitRequestDto reqDto, Long userId) {
        // === 1단계: 입력 데이터 유효성 검증 ===
        // 날짜 형식 검증 및 논리적 유효성 확인 (시작일 <= 종료일)
        HabitDateInfo dateInfo = validateAndParseHabitDates(reqDto);

        // === 2단계: 습관 및 액션 정보 준비 ===
        // habitId 존재 여부에 따른 분기 처리 -> 추후 존재하는 습관을 고쳐 사용하는 로직이 따로 필요할 수도 있기 때문에 Factory 패턴 적용
        HabitCreationInfo habitInfo = creationFactory.createHabit(reqDto);


        // === 3단계: 사용자 습관 생성 ===
        // 사용자와 습관을 연결하는 UserHabit 엔티티 생성
        UserHabit userHabit = UserHabit.builder()
                .userId(userId)         // 요청한 사용자 ID
                .habitId(habitInfo.getHabitId())       // 습관 ID (기존 또는 새로 생성된)
                .startDate(dateInfo.getStartDate())   // 습관 시작일
                .endDate(dateInfo.getEndDate())       // 습관 종료일
                .build();
        
        // UserHabit을 DB에 저장하고 생성된 ID 반환
        Long userHabitId = habitQuery.saveUserHabit(userHabit);

        // === 4단계: 사용자별 액션 상태 초기화 ===
        // 각 액션에 대해 사용자별 실행 상태를 관리하는 UserAction 엔티티들을 생성
        createUserActionsForHabit(habitInfo, userHabitId);

        // 생성된 사용자 습관 ID 반환
        return userHabitId;
    }

    /**
     * 습관의 모든 액션에 대해 사용자별 실행 상태를 초기화합니다.
     * 
     * @param habitInfo 습관 정보 (습관 ID와 액션 목록 포함)
     * @param userHabitId 방금 생성된 사용자 습관 ID
     */
    private void createUserActionsForHabit(HabitCreationInfo habitInfo, Long userHabitId) {
        // 각 액션에 대해 사용자별 실행 상태를 관리하는 UserAction 엔티티 목록 생성
        // LinkedList 사용: 순차적 추가가 많고 인덱스 접근이 없어 적합
        List<UserAction> userActions = new LinkedList<>();

        // 습관에 포함된 모든 액션에 대해 UserAction 생성
        // 초기 상태는 모두 미완료("N")로 설정
        for (Action action : habitInfo.getActions()) {
            userActions.add(
                    UserAction.builder()
                            .userHabitId(userHabitId)    // 사용자 습관과 연결
                            .actionId(action.getId())    // 원본 액션과 연결
                            .isCompleted(YN.N)            // 초기 완료 상태: 미완료
                            .build()
            );
        }

        // 배치 저장으로 성능 최적화
        // application.yml의 hibernate.jdbc.batch_size=50 설정과
        // rewriteBatchedInserts=true 옵션을 활용한 Bulk Insert
        habitQuery.saveUserActionList(userActions);
    }

    /**
     * 습관 시작일과 종료일의 유효성을 검증하고 LocalDate 객체로 변환합니다.
     * 
     * 검증 항목:
     * 1. null 체크
     * 2. 날짜 형식 검증 (yyyyMMdd)
     * 3. 시작일 <= 종료일 논리적 유효성 검증
     * 
     * @param reqDto 사용자 요청 데이터
     * @return 검증된 날짜 정보 객체
     * @throws CustomException 날짜가 null이거나 형식이 잘못된 경우
     * @throws CustomException 시작일이 종료일보다 늦은 경우
     */
    private HabitDateInfo validateAndParseHabitDates(CustomHabitRequestDto reqDto) {
        // 필수 입력값 null 체크
        if (reqDto.getStartDate() == null || reqDto.getEndDate() == null) {
            throw new CustomException(ErrorCode.DATE_FORMAT_ERROR);
        }

        // 문자열 날짜를 LocalDate로 파싱
        // 형식: "yyyyMMdd" (예: "20241005")
        // DateTimeParseException 발생 시 상위로 전파됨
        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(reqDto.getStartDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
            endDate = LocalDate.parse(reqDto.getEndDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (DateTimeParseException e) {
            throw new CustomException(ErrorCode.DATE_FORMAT_ERROR);
        }

        // 논리적 유효성 검증: 시작일이 종료일보다 늦으면 안됨
        if (startDate.isAfter(endDate)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        
        // TODO: 추가 검증 필요
        // - 시작일이 과거 날짜인지 확인
        // - 최대 습관 기간 제한 (예: 1년)
        // - 공휴일/주말 제외 옵션 등
        
        return new HabitDateInfo(startDate, endDate);
    }
}
