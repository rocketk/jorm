package io.github.rocketk.jorm;

import io.github.rocketk.jorm.conf.Config;
import io.github.rocketk.jorm.executor.DefaultSqlExecutor;
import io.github.rocketk.jorm.executor.SqlExecutor;
import io.github.rocketk.jorm.listener.metrics.MetricsExecutedListener;
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
    private SqlExecutor sqlExecutor;

    public Jorm(DataSource ds) {
        this(ds, defaultConfig());
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
        if (config.getMeterRegistry() != null) {
            sqlExecutor = new DefaultSqlExecutor(true, new MetricsExecutedListener(config.getMeterRegistry(), config.getSqlTagMapper()));
        }
    }

    public <T> Query<T> query(Class<T> model) {
        if (model != null) {
            if (Long.class.isAssignableFrom(model)) {
                return (Query<T>) new QueryInstance<>(ds, config, Long.class, rowMapperFactory, sqlExecutor).rowMapper((rs, cols) -> rs.getLong(1));
            }
            if (Integer.class.isAssignableFrom(model)) {
                return (Query<T>) new QueryInstance<>(ds, config, Integer.class, rowMapperFactory, sqlExecutor).rowMapper((rs, cols) -> rs.getInt(1));
            }
            if (Boolean.class.isAssignableFrom(model)) {
                return (Query<T>) new QueryInstance<>(ds, config, Boolean.class, rowMapperFactory, sqlExecutor).rowMapper((rs, cols) -> rs.getBoolean(1));
            }
            if (String.class.isAssignableFrom(model)) {
                return (Query<T>) new QueryInstance<>(ds, config, String.class, rowMapperFactory, sqlExecutor).rowMapper((rs, cols) -> rs.getString(1));
            }
        }
        return new QueryInstance<>(ds, config, model, rowMapperFactory, sqlExecutor);
    }

    public <T> Query<T> rawQuery(Class<T> model, String rawSql, Object... args) {
        return new QueryInstance<>(ds, config, model, rowMapperFactory, sqlExecutor).rawSql(rawSql, args);
    }

    public Query<Map> queryMap() {
        return new QueryInstance<>(ds, config, Map.class, rowMapperFactory, sqlExecutor);
    }

    public <T> Mutation<T> mutation(Class<T> model) {
        return new MutationInstance<>(ds, config, model, rowMapperFactory, sqlExecutor);
    }

    public Transaction transaction() {
        return new Transaction(ds, config, rowMapperFactory, sqlExecutor);
    }
}
