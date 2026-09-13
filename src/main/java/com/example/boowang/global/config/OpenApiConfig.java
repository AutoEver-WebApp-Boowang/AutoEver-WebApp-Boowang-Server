package com.example.boowang.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Swagger 화면의 제목과 JWT 인증 방식을 설정한다.
@Configuration
public class OpenApiConfig {

    // Swagger의 Authorize 버튼에서 사용할 인증 방식의 이름이다.
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    // @Bean으로 등록하면 springdoc이 이 설정을 Swagger 문서에 반영한다.
    @Bean
    public OpenAPI boowangOpenApi() {
        // Authorization: Bearer {JWT} 형태로 토큰을 보내도록 정의한다.
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Swagger 화면에 표시할 프로젝트 정보와 JWT 인증 방식을 등록한다.
        return new OpenAPI()
                .info(new Info()
                        .title("Boowang API")
                        .description("Boowang 백엔드 API 명세")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, bearerAuth));
    }
}
