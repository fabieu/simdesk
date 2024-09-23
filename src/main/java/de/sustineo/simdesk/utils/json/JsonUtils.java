package de.sustineo.simdesk.utils.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class JsonUtils {
    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        JsonUtils.objectMapper = objectMapper;
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
        return objectMapper.readValue(json, valueType);
    }

    @SneakyThrows
    public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
        return objectMapper.readValue(json, valueTypeRef);
    }

    @SneakyThrows
    public static <T> T fromJson(InputStream inputStream, Class<T> valueType) {
        return objectMapper.readValue(inputStream, valueType);
    }

    @SneakyThrows
    public static <T> T fromJson(InputStream inputStream, TypeReference<T> valueTypeRef) {
        return objectMapper.readValue(inputStream, valueTypeRef);
    }
}
