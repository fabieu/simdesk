package de.sustineo.simdesk.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecuritySchemes({
        @SecurityScheme(
                name = OpenApiConfiguration.SECURITY_REQUIREMENT_API_KEY_HEADER,
                type = SecuritySchemeType.APIKEY,
                in = SecuritySchemeIn.HEADER,
                paramName = "X-API-KEY"
        ),
        @SecurityScheme(
                name = OpenApiConfiguration.SECURITY_REQUIREMENT_API_KEY_BEARER,
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "API Key"
        )
})
public class OpenApiConfiguration {
    public static final String SECURITY_REQUIREMENT_API_KEY_HEADER = "ApiKeyHeaderAuth";
    public static final String SECURITY_REQUIREMENT_API_KEY_BEARER = "ApiKeyBearerAuth";

    @Bean
    public OpenAPI customOpenAPI(BuildProperties buildProperties) {
        License license = new License()
                .name("Apache 2.0")
                .url(Reference.GITHUB_LICENSE);

        Info info = new Info()
                .title(buildProperties.getName())
                .version(buildProperties.getVersion())
                .license(license);

        return new OpenAPI().info(info);
    }

    @Bean
    public GroupedOpenApi driverApi() {
        return GroupedOpenApi.builder()
                .group("v1")
                .displayName("Version 1")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
