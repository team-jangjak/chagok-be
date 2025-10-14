package com.jangjak.chagok.habit.controller.docs;

import com.jangjak.chagok.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "습관 관리 (HabitController)", description = "습관 관련 기능 제공")
@RequestMapping("/habit")
public interface HabitControllerDocs {


    /**
     * 인기 습관 카테고리 정보 조회
     */
    @Operation(
            summary = "인기 습관 카테고리 조회",
            description = """
                    인기 있는 습관 카테고리 목록을 조회합니다.
                    ### 응답 설명
                    - `data`는 카테고리 리스트입니다.
                    - 각 항목에는 `categoryId`, `categoryName`이 포함됩니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인기 습관 카테고리 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                            {
                                              "status": 200,
                                              "message": "인기 습관 카테고리가 조회되었습니다.",
                                              "data": [
                                                {
                                                  "categoryId": 1,
                                                  "categoryName": "운동"
                                                },
                                                {
                                                  "categoryId": 2,
                                                  "categoryName": "독서"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "오류 예시",
                                    value = """
                                            {
                                              "status": 500,
                                              "message": "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/popular-habit-category")
    ResponseEntity<?> getPopularHabitCategory();
}
