package com.jangjak.chagok.habit.controller.docs;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "OpenAI API", description = "OpenAI API를 통한 액션 추천")
@RequestMapping("/ai")
public interface OpenAIControllerDocs {
}
