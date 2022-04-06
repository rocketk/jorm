package com.github.rocketk.jorm.json;

import com.github.rocketk.jorm.json.fastjson.FastjsonMapper;
import com.github.rocketk.jorm.json.gson.GsonMapper;
import com.github.rocketk.jorm.json.jackson.JacksonMapper;

/**
 * @author pengyu
 * @date 2022/3/24
 */
public class JsonMapperFactory {
    public static JsonMapper getJsonMapper(final JsonProvider jsonProvider) {
        if (jsonProvider == null) {
            return new JacksonMapper();
        }
        switch (jsonProvider) {
            case GSON:
                return new GsonMapper();
            case FASTJSON:
                return new FastjsonMapper();
            case JACKSON:
            default:
                return new JacksonMapper();
        }
    }
}
