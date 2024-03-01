package com.example.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class SwaggerConfig {

    @Getter @Setter @AllArgsConstructor
    static class ErrorExample{
        String exampleName;
        String error;
        String error_description;
    }

    @Value("${error.exception.SignatureException}")
    private String signatureError;
    @Value("${message.exception.SignatureException}")
    private String signatureMessage;


    @Value("${error.exception.ExpiredJwtException}")
    private String expiredJwtError;
    @Value("${message.exception.ExpiredJwtException}")
    private String expiredJwtMessage;

    @Value("${error.exception.UsernameNotFoundException}")
    private String userNameNotFoundError;

    @Value("${error.exception.BadCredentialsException}")
    String badCredentialsError;

    @Value("${message.exception.UsernameNotFoundException}")
    private String userNameNotFoundMessage;

    @Value("${message.exception.BadCredentialsException}")
    String badCredentialsMessage;

    @Value("${error.exception.TokenRefreshFailException}")
    private String tokenRefreshFailError;

    @Value("${message.exception.TokenRefreshFailException}")
    String tokenRefreshFailMessage;


    @Bean
    public OpenAPI baseOpenApi() {

        ApiResponse jwtEx = createErrorApiResponse(new ErrorExample("InvalidSignature", signatureError, signatureMessage),
                                                new ErrorExample("ExpiredJwt", expiredJwtError, expiredJwtMessage));

        ApiResponse loginEx = createErrorApiResponse(new ErrorExample("EmailNotFound", userNameNotFoundError, userNameNotFoundMessage),
                                                new ErrorExample("IncorrectPassword",badCredentialsError, badCredentialsMessage));

        ApiResponse refreshEx = createErrorApiResponse(new ErrorExample("TokenRefreshFail", tokenRefreshFailError, tokenRefreshFailMessage));



        Components components = new Components();
        components.addResponses("jwtEx", jwtEx);
        components.addResponses("loginEx", loginEx);
        components.addResponses("refreshEx", refreshEx);

        return new OpenAPI()
                        .components(components)
                        .info(new Info()
                        .title("2024학년도 1학기 가톨릭대 종합설계 API Docs")
                        .version("1.0.0"));
    }

    private ApiResponse createErrorApiResponse(ErrorExample... examples) {
        MediaType jwtExMedia = new MediaType();
        for (ErrorExample ex : examples) {
            jwtExMedia.addExamples(ex.getExampleName(), new Example().value("{\"error\" : \"" + ex.getError() + "\", \"error_description\" : \"" + ex.getError_description() + "\"}"));
        }

        ApiResponse jwtEx = new ApiResponse().content(
                new Content().addMediaType("application/json", jwtExMedia)
        );
        return jwtEx;
    }

}
