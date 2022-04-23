package com.github.rocketk.jorm.conf;


import com.github.rocketk.jorm.dialect.Dialect;
import com.github.rocketk.jorm.dialect.StandardLimitOffsetAppender;

import static com.github.rocketk.jorm.json.JsonProvider.JACKSON;

/**
 * @author pengyu
 * @date 2022/3/24
 */
public class ConfigFactory {
    public static Config defaultConfig() {
        final Config config = new Config();
        config.setJsonProvider(JACKSON);
        config.setArrayDelimiter(" ");
        config.setPrintSql(true);
        config.setDialect(Dialect.STANDARD);
        return config;
    }
}
