package de.sustineo.acc.leaderboard.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String toJson(Object entity) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(entity);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }
}
