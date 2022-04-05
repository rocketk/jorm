package com.github.rocketk.jorm.json;

import com.github.rocketk.jorm.json.fastjson.FastjsonMapper;
import com.github.rocketk.jorm.json.gson.GsonMapper;
import com.github.rocketk.jorm.json.jackson.JacksonMapper;
import org.apache.commons.lang3.StringUtils;

/**
 * @author pengyu
 * @date 2022/3/24
 */
public class JsonMapperFactory {
    public static JsonMapper getJsonMapper(final String jsonProvider) {
        if ("jackson".equalsIgnoreCase(jsonProvider) || StringUtils.isBlank(jsonProvider)) {
            return new JacksonMapper();
        }
        if ("gon".equalsIgnoreCase(jsonProvider)) {
            return new GsonMapper();
        }
        if ("fastjson".equalsIgnoreCase(jsonProvider)) {
            return new FastjsonMapper();
        }
        throw new IllegalArgumentException("no such JsonMapper for " + jsonProvider);
    }
}