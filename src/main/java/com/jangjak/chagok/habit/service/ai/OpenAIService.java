package com.jangjak.chagok.habit.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.common.exception.CustomException;
import com.jangjak.chagok.common.exception.ErrorCode;
import com.jangjak.chagok.habit.dto.request.HabitForActionReqDto;
import com.jangjak.chagok.habit.dto.request.ActionToAIReqDto;
import com.jangjak.chagok.habit.dto.response.ActionDto;
import com.jangjak.chagok.habit.repository.HabitCategoryRepository;
import com.jangjak.chagok.user.entity.User;
import com.jangjak.chagok.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;


@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAIService {

    private static final String TEMPLATE = """
            당신의 목표는 **사용자의 습관 주제(habit_title)**와 **카테고리(category)**를 기반으로,
            실행 가능한 주간 계획을 구체적으로 설계하는 것입니다.
            
            [입력값 설명]
            - habit_title: 사용자가 설정한 습관 주제 (예: '30분 이상 러닝하기', '아침 독서하기', '물 2L 마시기')
            - category: 습관 카테고리 
            - frequency: 주당 실행 횟수 (예: 주 3회)
            - propensity_score: 사용자의 성향 점수 (0~100, 낮을수록 계획 실행이 어려움)
            - age: 사용자의 나이
            - weeks_count: 생성할 주차 수
            - etc: 추가 조건 또는 사용자의 선호 사항 (예: '주말 위주', '대학생 수준의 계획')
            
            [입력값]
            - habit_title: "%s"
            - category: "%s"
            - frequency: %d
            - propensity_score: %d
            - age: %d
            - etc: %s
            - weeks_count: %d
            
            [주기 설정 규칙]
            - 모든 계획은 ‘일주일 단위’(weekly) 로 생성됩니다.
            - 각 주의 수행 빈도(freq)는 입력값 frequency를 그대로 사용합니다.
            - weeks_count는 생성할 주차 수를 의미하며, 1주차부터 순차적으로 계획을 구성합니다.
            
            [습관 의미 해석 규칙]
            - habit_title은 습관 계획의 핵심 기준으로, 실제 실행 행동의 내용을 결정합니다.
            - category는 habit_title의 의미를 이해하기 위한 보조 정보로 활용되며,
              habit_title이 모호하거나 다의적일 경우 해석을 보완하는 용도로 사용됩니다.
            - 따라서 계획 생성 시 habit_title을 중심으로 행동을 구체화하되,
              category는 행동의 방향성을 자연스럽게 유지하도록 참고합니다.
            
            [행동 일관성 규칙]
            - tasks는 category에 해당하는 행동만 포함해야 합니다.
            - habit_title의 핵심 동사(예: 읽기, 걷기, 쓰기, 달리기 등)를 중심으로 실제 실행 가능한 행동으로 구체화합니다.
            - 실행 불가능하거나 과도하게 세세한 지시(예: 장소, 구도 등)는 피하세요.
            
            [구성 규칙]
            - 모든 주의 freq = frequency이며, 1~7 사이의 정수로 유지해야 합니다.
            - tasks 배열의 길이는 freq와 동일해야 합니다.
            - reason에는 해당 주차 계획의 설계 의도를 구체적으로 설명하세요 (예: 강도 조절, 시간대, 성향 반영 등).
            
            [출력 포맷]
            아래 JSON 형식만 출력하세요. 추가 텍스트, 주석, 코드 블록은 금지입니다.
            
            {
              "weeks": [
                {
                  "week": number,              // 1부터 시작
                  "freq": number(1~7),
                  "tasks": ["문장", "문장", ...],   // 길이=freq
                  "reason": "문장"               // 설계 의도 설명
                },
                ...
              ]
            }
            
            [검증 조건]
            - tasks 내용이 category에 부합하는가?
            - reason이 비어 있지 않고, 사용자의 propensity_score나 etc 조건을 반영했는가?
            - 모든 텍스트가 자연스러운 한국어로 작성되었는가?
            
            """;


    private final ChatClient chatClient;
    private final UserRepository userRepository;
    private final HabitCategoryRepository habitCategoryRepository;
    private final ObjectMapper objectMapper;


    /**
     * AI에게 프롬프트를 보내 PlanDto를 생성 및 검증 후 반환.
     */
    public ActionDto actionSuggest(TokenUserInfo userInfo, HabitForActionReqDto dto) {

        //성향점수, 나이
        User user = userRepository.findById(userInfo.getId()).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND));

        LocalDate birthDate = user.getBirthDate();
        LocalDate now = LocalDate.now();

        int age = Period.between(birthDate, now).getYears();

        String category = habitCategoryRepository.findCategoryNameByHabitId(dto.getHabitId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        ActionToAIReqDto actionToAIReqDto = ActionToAIReqDto.builder().age(age).tendency(user.getTendency()).categoryName(category).build();

        try {
            String prompt = String.format(
                    TEMPLATE,
                    Objects.requireNonNullElse(dto.getHabitTitle(), ""),
                    Objects.requireNonNullElse(actionToAIReqDto.getCategoryName(), ""),
                    Objects.requireNonNullElse(dto.getFrequency(), 0),
                    Objects.requireNonNullElse(actionToAIReqDto.getTendency(), 0),
                    Objects.requireNonNullElse(actionToAIReqDto.getAge(), 20),
                    Objects.requireNonNullElse(dto.getEtc(), ""),
//                    Objects.requireNonNullElse(dto.getVerificationMethod(), ""),
                    Objects.requireNonNullElse(dto.getWeeksCount(), 8)

            );

            String content = chatClient
                    .prompt()
                    .system("당신은 현실적인 루틴 설계 전문가입니다. 반드시 지정된 JSON 형식을 지키고, 허구를 만들지 않습니다. " +
                            "당신은 사용자의 나이, 직업, 성향 점수, 그리고 습관 주제(habit_title)를 기반으로 현실적으로 지속 가능한 루틴을 설계하는 습관 코치입니다.")
                    .user(prompt)
                    .options(OpenAiChatOptions.builder()
                            .model("gpt-4.1-mini")
                            .temperature(0.3)
                            .topP(0.9)
                            .frequencyPenalty(0.2)
                            .presencePenalty(0.2)
                            .build())
                    .call()
                    .content();

            log.info("실제 전송된 요청: {}", prompt);

            if (content == null || content.isBlank()) {
                log.warn("AI 응답이 비어 있음");
                throw new CustomException(ErrorCode.AI_RESPONSE_ERROR);
            }

            ActionDto plan = objectMapper.readValue(content.trim(), ActionDto.class);

            // DTO 검증
            if (plan.getWeeks() == null || plan.getWeeks().isEmpty()) {
                throw new CustomException(ErrorCode.AI_RESPONSE_MAPPING_ERROR);
            }

            return plan;

        } catch (Exception e) {
            log.error("AI 계획 생성 중 오류 발생", e);
            throw new CustomException(ErrorCode.AI_REQUEST_ERROR);
        }
    }


}
