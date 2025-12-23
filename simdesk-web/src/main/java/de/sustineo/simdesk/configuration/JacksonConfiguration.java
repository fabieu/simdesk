package de.sustineo.simdesk.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.sustineo.simdesk.utils.json.BooleanDeserializer;
import discord4j.common.JacksonResources;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfiguration {
    @Bean
    public SimpleModule customJacksonModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Boolean.class, new BooleanDeserializer());
        return module;
    }


    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        return new Jackson2ObjectMapperBuilder().build();
    }

    @Profile(ProfileManager.PROFILE_DISCORD)
    @Qualifier("discord")
    @Bean
    public ObjectMapper objectMapperDiscord() {
        return JacksonResources.create().getObjectMapper();
    }
}
