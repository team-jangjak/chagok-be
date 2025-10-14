package com.jangjak.chagok.user.controller.docs;

import com.jangjak.chagok.common.dto.CommonResponse;
import com.jangjak.chagok.common.dto.TokenUserInfo;
import com.jangjak.chagok.user.dto.UserReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 관리 (UserController)", description = "사용자 관련 기능 제공")
@RequestMapping("/user")
public interface UserControllerDocs {

    /**
     * 회원가입
     */
    @Operation(
            summary = "소셜 회원가입",
            description = """
                    소셜(OAuth) 정보를 기반으로 신규 계정을 생성합니다.
                    ### 요청 본문
                    - `oauthId`(Long): OauthId 값
                    - `name`(String): 사용자 이름
                    - `email`(String): 이메일
                    - `birthDate`(LocalDate, yyyy-MM-dd)
                    - `profileImage`(String, URL)
                    - `tendency`(Integer): 성향 점수
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserReqDto.class),
                            examples = @ExampleObject(
                                    name = "요청 예시",
                                    value = """
                                            {
                                              "oauthId": 10,
                                              "name": "차곡유저",
                                              "email": "user@example.com",
                                              "birthDate": "2004-03-15",
                                              "profileImage": "https://cdn.chagok.shop/avatars/u42.png",
                                              "tendency": 73
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "status": 201,
                                              "message": "회원가입이 완료되었습니다.",
                                              "data": {
                                                "userId": 42,
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효성 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "요청 값이 유효하지 않습니다.",
                                              "data": {
                                                "email": "올바른 이메일 형식이 아닙니다.",
                                                "birthDate": "필수 값입니다."
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 가입된 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 409,
                                              "message": "이미 가입된 사용자입니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/sign-up")
    ResponseEntity<?> createSocialAuth(@Valid @RequestBody UserReqDto userReqDto);

    /**
     * 내 정보 조회
     */
    @Operation(
            summary = "내 정보 조회",
            description = """
                    로그인한 사용자의 프로필 정보를 조회합니다.
                    ### 인증
                    - **쿠키(auth cookie)** 필요: 서버가 설정한 `access_token`(예시) 쿠키를 사용합니다.
                    - Swagger UI에서 cookieAuth로 인증 후 호출하세요.
                    """,
            security = @SecurityRequirement(name = "cookieAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "status": 200,
                                              "message": "사용자 정보가 조회되었습니다.",
                                              "data": {
                                                "name": "차곡유저",
                                                "email": "user@example.com",
                                                "birthDate": "2004-03-15",
                                                "profileImage": "https://cdn.chagok.shop/avatars/u42.png",
                                                "tendency": 73
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패(쿠키 없음/만료/무효)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 401,
                                              "message": "인증되지 않은 사용자입니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 403,
                                              "message": "해당 자원에 접근할 권한이 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/info")
     ResponseEntity<?> getInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo
    );

    /**
     * 이메일 중복 확인
     */
    @Operation(
            summary = "이메일 중복 확인",
            description = "이메일 사용 가능 여부를 확인합니다.",
            parameters = {
                    @Parameter(
                            name = "email",
                            description = "중복 확인할 이메일",
                            example = "user@example.com",
                            required = true
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "확인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "사용 가능",
                                    value = """
                                            {
                                              "status": 200,
                                              "message": "해당 이메일은 사용 가능합니다.",
                                              "data": { "available": true }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 사용 중",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "사용 불가",
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "이미 사용 중인 이메일입니다.",
                                              "data": { "available": false }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "email 파라미터가 누락되었거나 형식이 잘못되었습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/email-check")
    ResponseEntity<?> emailCheck(
            @RequestParam String email
    );
}
