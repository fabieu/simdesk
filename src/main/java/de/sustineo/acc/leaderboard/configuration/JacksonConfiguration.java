package de.sustineo.acc.leaderboard.configuration;

import com.fasterxml.jackson.databind.module.SimpleModule;
import de.sustineo.acc.leaderboard.utils.json.BooleanDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {
    @Bean
    public SimpleModule customJacksonModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Boolean.class, new BooleanDeserializer());
        return module;
    }
}
