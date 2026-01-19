package de.sustineo.simdesk.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@TestConfiguration
public class TestRestClientConfiguration {
    @Bean
    RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost") // never actually called
                .build();
    }
}
