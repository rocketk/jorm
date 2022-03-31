package com.github.rocketk.jorm.json.jackson;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.rocketk.jorm.json.JsonException;
import com.github.rocketk.jorm.json.JsonMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * @author pengyu
 * @date 2021/12/22
 */
public class JacksonMapper implements JsonMapper {

    private final ObjectMapper om = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.ALWAYS);

    @Override
    public <T> T unmarshal(String content, Class<T> clazz) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        try {
            return om.readValue(content, clazz);
        } catch (Exception e) {
            throw new JsonException(e.getMessage(), e);
        }
    }

    @Override
    public String marshal(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return om.writeValueAsString(obj);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

}
