package com.github.rocketk.jorm.json.gson;

import com.github.rocketk.jorm.json.JsonMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

/**
 * @author pengyu
 * @date 2021/12/22
 */
public class GsonMapper implements JsonMapper {
    private final Gson gson = new Gson();
    @Override
    public <T> T unmarshal(String content, Class<T> clazz) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        return gson.fromJson(content, clazz);
    }

    @Override
    public String marshal(Object obj) {
        if (obj == null) {
            return null;
        }
        return gson.toJson(obj);
    }
}