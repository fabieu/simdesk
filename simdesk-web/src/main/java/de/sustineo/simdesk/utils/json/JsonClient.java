package de.sustineo.simdesk.utils.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public final class JsonClient implements ApplicationContextAware {
    private static ObjectMapper objectMapper;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        JsonClient.objectMapper = applicationContext.getBean(ObjectMapper.class);
    }

    @SneakyThrows
    public static String toJson(Object entity) {
        return objectMapper.writeValueAsString(entity);
    }

    @SneakyThrows
    public static String toJsonPretty(Object entity) {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
    }

    @SneakyThrows
    public static <T> T fromJson(String json, Class<T> valueType) {
        if (json == null) {
            return null;
        }

        return objectMapper.readValue(json, valueType);
    }

    public static boolean isValid(String json) {
        try {
            objectMapper.readTree(json);
        } catch (JacksonException e) {
            return false;
        }
        return true;
    }
}
