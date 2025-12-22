package de.sustineo.simdesk.configuration;

import discord4j.common.JacksonResources;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Profile(ProfileManager.PROFILE_DISCORD)
    @Qualifier("discord")
    public RestTemplate discordRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate();

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(JacksonResources.create().getObjectMapper());
        restTemplate.getMessageConverters().addFirst(converter);

        return restTemplate;
    }
}
