package io.github.rocketk.jorm.json.fastjson;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

/**
 * @author pengyu
 */
public class FastjsonMapper implements io.github.rocketk.jorm.json.JsonMapper {
    @Override
    public <T> T unmarshal(String content, Class<T> clazz) {
        if (StringUtils.isBlank(content)) {
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
