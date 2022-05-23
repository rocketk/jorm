package io.github.rocketk.jorm.conf;


import io.github.rocketk.jorm.dialect.Dialect;

import static io.github.rocketk.jorm.json.JsonProvider.JACKSON;

/**
 * @author pengyu
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
