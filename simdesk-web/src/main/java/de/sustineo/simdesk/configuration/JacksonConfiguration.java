package de.sustineo.simdesk.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sustineo.simdesk.utils.json.BooleanDeserializer;
import discord4j.common.JacksonResources;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfiguration {
    @Bean
    public SimpleModule customJacksonModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Boolean.class, new BooleanDeserializer());
        module.addDeserializer(boolean.class, new BooleanDeserializer());
        return module;
    }

    @Bean
    JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL))
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER);
    }

    @Profile(SpringProfile.DISCORD)
    @Bean
    @Qualifier("discord")
    public ObjectMapper objectMapperDiscord() {
        return JacksonResources.create().getObjectMapper();
    }
}
