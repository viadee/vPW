package de.viadee.vpw.pipeline.service.json;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonMapper {

    private final ObjectMapper objectMapper;

    public JsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonMapperException("Failed to serialize object as JSON string", e);
        }
    }

    public byte[] toJsonAsByteArray(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new JsonMapperException("Failed to serialize object as JSON byte array", e);
        }
    }

    public <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (IOException e) {
            throw new JsonMapperException("Failed to deserialize JSON string", e);
        }
    }

    private class JsonMapperException extends RuntimeException {

        private JsonMapperException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
