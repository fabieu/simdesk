package de.sustineo.acc.servertools.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class PropertyConfiguration {
    @Bean // Activates new configuration service which supports converting String to Collection types.
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }
}
