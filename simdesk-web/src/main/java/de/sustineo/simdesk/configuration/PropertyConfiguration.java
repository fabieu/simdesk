package de.sustineo.simdesk.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class PropertyConfiguration {
    // Activate new configuration service which supports converting String to Collection types.
    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}
