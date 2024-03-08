package de.sustineo.acc.servertools.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class JsonUtils {
    private final ObjectMapper objectMapper;

    public JsonUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public String toJson(Object entity) throws JsonProcessingException {
        return objectMapper.writeValueAsString(entity);
    }

    public String toJsonPretty(Object entity) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
    }

    public <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }

    public <T> T fromJson(InputStream inputStream, Class<T> clazz) throws IOException {
        return objectMapper.readValue(inputStream, clazz);
    }
}
