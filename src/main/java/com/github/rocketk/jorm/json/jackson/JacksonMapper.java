package com.github.rocketk.jorm.json.jackson;

import com.github.rocketk.jorm.json.JsonMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author pengyu
 * @date 2021/12/22
 */
public class JacksonMapper implements JsonMapper {

    private final ObjectMapper om = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    public <T> T unmarshal(String content, Class<T> clazz) {
        if (content == null) {
            return null;
        }
        try {
            return om.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String marshal(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
