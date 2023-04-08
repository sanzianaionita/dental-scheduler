package com.example.dentalscheduler.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(
                        "bearer",
                        new io.swagger.v3.oas.models.security.SecurityScheme()
                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearer", Arrays.asList("read", "write")))
                .info(new Info()
                        .title("Softbinator Labs 2023 OpenAPI")
                        .description("An OpenAPI documentation for the Softbinator Labs 2023 course.")
                        .version("V1.0"));
    }

//    public static final String AUTHORIZATION_HEADER = "Authorization";
//
//    private ApiInfo apiInfo() {
//        return new ApiInfo("Dental Scheduler REST API",
//                "Dental Scheduler rest api description.",
//                "1.0",
//                "",
//                new Contact("Sanziana Ionita", "", "sbituleanu@yahoo.com"),
//                "",
//                "",
//                Collections.emptyList());
//    }
//
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .securityContexts(Collections.singletonList(securityContext()))
//                .securitySchemes(List.of(apiKey()))
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.example.dentalscheduler"))
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    private ApiKey apiKey() {
//        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
//    }
//
//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//                .securityReferences(defaultAuth())
//                .build();
//    }
//
//    private List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return List.of(new SecurityReference("JWT", authorizationScopes));
//    }
}
