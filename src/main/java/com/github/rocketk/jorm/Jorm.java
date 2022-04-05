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

    public <T> Query<T> query(Class<T> model) {
        return new QueryInstance<>(ds, config, model);
    }

    public <T> Update<T> update(Class<T> model) {
        return new UpdateInstance<>(ds, config, model);
    }
}
