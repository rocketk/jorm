package com.github.rocketk.jorm.json.fastjson;

import com.alibaba.fastjson.JSON;
import com.github.rocketk.jorm.json.JsonMapper;

/**
 * @author pengyu
 * @date 2021/12/22
 */
public class FastjsonMapper implements com.github.rocketk.jorm.json.JsonMapper {
    @Override
    public <T> T unmarshal(String content, Class<T> clazz) {
        if (content == null) {
            return null;
        }
        return JSON.parseObject(content, clazz);
    }

    @Override
    public String marshal(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj);
    }
}
