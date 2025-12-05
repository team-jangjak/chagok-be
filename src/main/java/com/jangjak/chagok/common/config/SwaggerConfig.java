package com.jangjak.chagok.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi chagokApi() {
        return GroupedOpenApi.builder()
                .group("chagok")
                .packagesToScan("com.jangjak.chagok")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI chagokOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("차곡 API 명세서")
                        .description("AI 습관 형성 어플리케이션, 차곡의 Swagger입니다.")
                        .version("0.0.1-SNAPSHOT"));
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build(); // 자동 주입된 Builder로 ChatClient 생성
    }
}
