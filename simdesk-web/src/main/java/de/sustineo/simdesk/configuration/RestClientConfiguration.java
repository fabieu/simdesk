package de.sustineo.simdesk.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Primary
    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }

    @Profile(ProfileManager.PROFILE_DISCORD)
    @Qualifier("discord")
    @Bean
    public RestClient restClientDiscord(@Qualifier("discord") ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);

        return RestClient.builder()
                .messageConverters(converters -> {
                    converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
                    converters.addFirst(jacksonConverter);
                })
                .build();
    }
}
