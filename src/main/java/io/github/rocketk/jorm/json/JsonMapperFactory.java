package io.github.rocketk.jorm.json;

import io.github.rocketk.jorm.json.fastjson.FastjsonMapper;
import io.github.rocketk.jorm.json.gson.GsonMapper;
import io.github.rocketk.jorm.json.jackson.JacksonMapper;

/**
 * @author pengyu
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
