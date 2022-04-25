package com.github.rocketk.jorm;

import com.github.rocketk.jorm.conf.Config;
import com.github.rocketk.jorm.mapper.row.DefaultRowMapperFactory;
import com.github.rocketk.jorm.mapper.row.RowMapperFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.rocketk.jorm.conf.ConfigFactory.defaultConfig;

/**
 * @author pengyu
 * @date 2021/12/12
 */
public class Jorm {
    private final DataSource ds;
    private final Config config;
    private RowMapperFactory rowMapperFactory;

    public Jorm(DataSource ds) {
        this.ds = ds;
        this.config = defaultConfig();
        init();
    }

    public Jorm(DataSource ds, Config config) {
        this.ds = ds;
        this.config = config;
        init();
    }

    private void init() {
        if (rowMapperFactory == null) {
            rowMapperFactory = new DefaultRowMapperFactory(config.getArrayDelimiter(), config.getJsonProvider());
        }
    }

    public <T> Query<T> query(Class<T> model) {
        return new QueryInstance<>(ds, config, model, rowMapperFactory);
    }

    public <T> Query<T> rawQuery(Class<T> model, String rawSql, Object... args) {
        return new QueryInstance<>(ds, config, model, rowMapperFactory).rawSql(rawSql, args);
    }

    public Query<Map> queryMap() {
        return new QueryInstance<>(ds, config, Map.class, rowMapperFactory);
    }

    public <T> Mutation<T> mutation(Class<T> model) {
        return new MutationInstance<>(ds, config, model, rowMapperFactory);
    }

    public Transaction transaction() {
        return new Transaction(ds, config);
    }
}
