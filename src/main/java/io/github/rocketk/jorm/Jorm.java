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
    private static int instances = 0;
    private final String name;
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
        this.name = config.getName() == null ? "JORM-" + instances : config.getName();
        init();
        instances++;
    }


    private void init() {
        if (rowMapperFactory == null) {
            rowMapperFactory = new DefaultRowMapperFactory(config.getArrayDelimiter(), config.getJsonProvider());
        }
        final DefaultSqlExecutor executor = new DefaultSqlExecutor();
        executor.setEnablePrintSql(config.isEnablePrintSql());
        executor.setLowQueryThreshold(config.getLowQueryThreshold());
        executor.setEnableEvent(config.isEnableEvent());
        if (config.isEnableEvent() && config.getMeterRegistry() != null) {
            executor.setListener(new MetricsExecutedListener(config.getMeterRegistry(), config.getSqlTagMapper()));
        }
        this.sqlExecutor = executor;
//        if (config.getMeterRegistry() != null) {
//            sqlExecutor = new DefaultSqlExecutor(true, new MetricsExecutedListener(config.getMeterRegistry(), config.getSqlTagMapper()));
//        }
    }

    public <T> Query<T> query(Class<T> model) {
        if (model != null) {
            if (Long.class.isAssignableFrom(model)) {
                return (Query<T>) new QueryInstance<>(name, ds, config, Long.class, rowMapperFactory, sqlExecutor).rowMapper((rs, cols) -> rs.getLong(1));
            }
            if (Integer.class.isAssignableFrom(model)) {
                return (Query<T>) new QueryInstance<>(name, ds, config, Integer.class, rowMapperFactory, sqlExecutor).rowMapper((rs, cols) -> rs.getInt(1));
            }
            if (Boolean.class.isAssignableFrom(model)) {
                return (Query<T>) new QueryInstance<>(name, ds, config, Boolean.class, rowMapperFactory, sqlExecutor).rowMapper((rs, cols) -> rs.getBoolean(1));
            }
            if (String.class.isAssignableFrom(model)) {
                return (Query<T>) new QueryInstance<>(name, ds, config, String.class, rowMapperFactory, sqlExecutor).rowMapper((rs, cols) -> rs.getString(1));
            }
        }
        return new QueryInstance<>(name, ds, config, model, rowMapperFactory, sqlExecutor);
    }

    public <T> Query<T> rawQuery(Class<T> model, String rawSql, Object... args) {
        return new QueryInstance<>(name, ds, config, model, rowMapperFactory, sqlExecutor).rawSql(rawSql, args);
    }

    public Query<Map> queryMap() {
        return new QueryInstance<>(name, ds, config, Map.class, rowMapperFactory, sqlExecutor);
    }

    public <T> Mutation<T> mutation(Class<T> model) {
        return new MutationInstance<>(name, ds, config, model, rowMapperFactory, sqlExecutor);
    }

    public Transaction transaction() {
        return new Transaction(name, ds, config, rowMapperFactory, sqlExecutor);
    }

    public String getName() {
        if (config.getName() == null) {
            return toString();
        }
        return config.getName();
    }
}
