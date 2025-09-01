package org.atmgigi.hyobankingbe.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.atmgigi.hyobankingbe.common.exception.JsonConversionException;

import java.io.IOException;

public class JsonUtil {
    private JsonUtil() {}

    public static String toCanonicalJson(ObjectMapper mapper, Object obj) {
        try {
            return mapper.writer()
                    .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                    .without(SerializationFeature.INDENT_OUTPUT)
                    .writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonConversionException("JSON 직렬화 실패");
        }
    }
    public static <T> T toObject(ObjectMapper mapper, String json, Class<T> type) {
        try {
            if (json == null) return null;
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new JsonConversionException("JSON → " + type.getSimpleName() + " 변환 실패");
        }
    }
}
