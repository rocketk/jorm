package io.github.rocketk.jorm;

import io.github.rocketk.jorm.conf.Config;
import io.github.rocketk.jorm.mapper.row.DefaultRowMapperFactory;
import io.github.rocketk.jorm.mapper.row.RowMapperFactory;

import javax.sql.DataSource;
import java.util.Map;

import static io.github.rocketk.jorm.conf.ConfigFactory.defaultConfig;

/**
 * @author pengyu
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
        if (model != null) {
            if (Long.class.isAssignableFrom(model)) {
                return (Query<T>) new QueryInstance<>(ds, config, Long.class, rowMapperFactory).rowMapper((rs, cols) -> rs.getLong(1));
            }
            if (Integer.class.isAssignableFrom(model)) {
                return (Query<T>) new QueryInstance<>(ds, config, Integer.class, rowMapperFactory).rowMapper((rs, cols) -> rs.getInt(1));
            }
            if (Boolean.class.isAssignableFrom(model)) {
                return (Query<T>) new QueryInstance<>(ds, config, Boolean.class, rowMapperFactory).rowMapper((rs, cols) -> rs.getBoolean(1));
            }
            if (String.class.isAssignableFrom(model)) {
                return (Query<T>) new QueryInstance<>(ds, config, String.class, rowMapperFactory).rowMapper((rs, cols) -> rs.getString(1));
            }
        }
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
