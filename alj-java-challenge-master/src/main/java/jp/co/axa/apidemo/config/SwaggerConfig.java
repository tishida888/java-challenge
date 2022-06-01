package jp.co.axa.apidemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class SwaggerConfig {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * 'Authorization :' is appended in the key
     * @return
     */
    private ApiKey apiKey(){
        return new ApiKey(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER, "header");
    }

    /**
     * apiInfo :
     *    API basic information in Swagger top page.
     * @return
     */
    private ApiInfo apiInfo(){
        return new ApiInfo(
                "Axa Java Challenge - Spring Boot REST APIs",
                "Axa Java Challenge - Spring Boot REST API Documentation",
                "1",
                "Terms of service",
                new Contact("Tatsuya Ishida", "", "tishida@gmail.com"),
                "License of API",
                "API license URL",
                Collections.emptyList()
        );
    }

    @Bean
    public Docket api(){
        // User API description tag that are shown in Swagger UI
        Tag employeeTag = new Tag("Employee", "Employee API that provides view/add/update/delete actions through API.");
        Tag tokenTag = new Tag("Token", "Generate Token. The token is used for authentication and available multiple times. It doesn't need to regenerate again unless it is expired. ");
        Tag userTag = new Tag("Account", "API account management.\nAdmin role user can manage (view/add/delete) the user list through API.");

        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("jp.co.axa.apidemo.controllers"))
                .paths(PathSelectors.any())
                .build()
                .tags(employeeTag, tokenTag, userTag);
    }

    private SecurityContext securityContext(){
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth(){
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference(AUTHORIZATION_HEADER, authorizationScopes));
    }
}
