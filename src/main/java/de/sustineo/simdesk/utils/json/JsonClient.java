package de.sustineo.simdesk.utils.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class JsonClient {
    private final ObjectMapper objectMapper;

    public JsonClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public String toJson(Object entity) {
        return objectMapper.writeValueAsString(entity);
    }

    @SneakyThrows
    public String toJsonPretty(Object entity) {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
    }

    @SneakyThrows
    public <T> T fromJson(String json, Class<T> valueType) {
        return objectMapper.readValue(json, valueType);
    }

    @SneakyThrows
    public <T> T fromJson(InputStream inputStream, Class<T> valueType) {
        return objectMapper.readValue(inputStream, valueType);
    }

    public boolean isValid(String json) {
        try {
            objectMapper.readTree(json);
        } catch (JacksonException e) {
            return false;
        }
        return true;
    }
}
