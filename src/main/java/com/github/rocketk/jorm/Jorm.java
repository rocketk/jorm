package com.github.rocketk.jorm;

import com.github.rocketk.jorm.conf.Config;

import javax.sql.DataSource;

import static com.github.rocketk.jorm.conf.ConfigFactory.defaultConfig;

/**
 * @author pengyu
 * @date 2021/12/12
 */
public class Jorm {
    private DataSource ds;
    private Config config;

    public Jorm(DataSource ds) {
        this.ds = ds;
        this.config = defaultConfig();
    }

    public Jorm(DataSource ds, Config config) {
        this.ds = ds;
        this.config = config;
    }

    public <T> ModelQuery<T> query(Class<T> model) {
        return new JormModelQueryInstance<>(ds, config, model);
    }

    public <T> ModelQuery<T> rawQuery() {
        throw new RuntimeException("not implemented");
    }

    public <T> ModelUpdate<T> update(Class<T> model) {
        return new JormModelUpdateInstance<>(ds, config, model);
    }
}
