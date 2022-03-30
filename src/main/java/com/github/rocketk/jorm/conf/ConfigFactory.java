package com.github.rocketk.jorm.conf;

import static com.github.rocketk.jorm.conf.Config.JACKSON;

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
        return config;
    }
}
