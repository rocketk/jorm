package io.github.rocketk.jorm;

import io.github.rocketk.jorm.conf.Config;
import io.github.rocketk.jorm.datasource.StickyConnectionDataSourceWrapper;
import io.github.rocketk.jorm.datasource.TransactionalConnectionWrapper;
import io.github.rocketk.jorm.err.JormTransactionException;
import io.github.rocketk.jorm.executor.SqlExecutor;
import io.github.rocketk.jorm.mapper.row.DefaultRowMapperFactory;
import io.github.rocketk.jorm.mapper.row.RowMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.github.rocketk.jorm.conf.ConfigFactory.defaultConfig;

/**
 * @author pengyu
 */
public class Transaction {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String jormInstanceName;
    private Config config;
    private StickyConnectionDataSourceWrapper ds;
    private TransactionalConnectionWrapper singleConnection;
    private boolean hasRollback;
    private RowMapperFactory rowMapperFactory;
    private SqlExecutor sqlExecutor;

    private Consumer<Transaction> operations;
    private BiConsumer<Transaction, Exception> onErrorFunc = (t, e) -> {
        logger.error(e.getMessage(), e);
        t.rollback();
    };

    public Transaction(String jormInstanceName, DataSource ds) {
        this(jormInstanceName, ds, null);
    }

    public Transaction(String jormInstanceName, DataSource ds, Config config) {
        this(jormInstanceName, ds, config, null, null);
    }

    public Transaction(String jormInstanceName, DataSource ds, Config config, RowMapperFactory rowMapperFactory, SqlExecutor sqlExecutor) {
        this.jormInstanceName = jormInstanceName;
        this.ds = new StickyConnectionDataSourceWrapper(ds);
        try {
            singleConnection = (TransactionalConnectionWrapper) this.ds.getConnection();
        } catch (SQLException e) {
            throw new JormTransactionException(e);
        }
        this.config = config;
        this.rowMapperFactory = rowMapperFactory;
        this.sqlExecutor = sqlExecutor;
        init();
    }

    private void init() {
        if (config == null) {
            config = defaultConfig();
        }
        if (rowMapperFactory == null) {
            rowMapperFactory = new DefaultRowMapperFactory(config.getArrayDelimiter(), config.getJsonProvider());
        }
    }

    public <T> Query<T> query(Class<T> model) {
        return new QueryInstance<>(jormInstanceName, ds, config, model, rowMapperFactory, sqlExecutor);
    }

    public <T> Query<T> rawQuery(Class<T> model, String rawSql, Object... args) {
        return new QueryInstance<>(jormInstanceName, ds, config, model, rowMapperFactory, sqlExecutor).rawSql(rawSql, args);
    }

    public Query<Map> queryMap() {
        return new QueryInstance<>(jormInstanceName, ds, config, Map.class, rowMapperFactory, sqlExecutor);
    }

    public <T> Mutation<T> mutation(Class<T> model) {
        return new MutationInstance<>(jormInstanceName, ds, config, model, rowMapperFactory, sqlExecutor);
    }

    public Transaction operations(Consumer<Transaction> operation) {
        this.operations = operation;
        return this;
    }

    public Transaction onError(BiConsumer<Transaction, Exception> onErrorFunc) {
        this.onErrorFunc = onErrorFunc;
        return this;
    }

    public void rollback() {
        if (hasRollback) {
            return;
        }
        try {
            singleConnection.rollback();
            hasRollback = true;
        } catch (SQLException e) {
            throw new JormTransactionException(e);
        }
    }

    public boolean commit() {
        try {
            this.operations.accept(this);
            singleConnection.commit();
            return true;
        } catch (Exception e) {
            onErrorFunc.accept(this, e);
            return false;
        } finally {
            if (singleConnection != null) {
                try {
                    singleConnection.setAutoCommit(true);
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
                try {
                    singleConnection.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    singleConnection = null;
                }
            }
            ds = null;
        }
    }
}
