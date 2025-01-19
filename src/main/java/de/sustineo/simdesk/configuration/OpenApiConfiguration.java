package de.sustineo.simdesk.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
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
}
