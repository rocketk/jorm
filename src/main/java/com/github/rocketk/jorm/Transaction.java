package com.github.rocketk.jorm;

import com.github.rocketk.jorm.conf.Config;
import com.github.rocketk.jorm.datasource.StickyConnectionDataSourceWrapper;
import com.github.rocketk.jorm.datasource.TransactionalConnectionWrapper;
import com.github.rocketk.jorm.err.JormTransactionException;
import com.github.rocketk.jorm.mapper.row.DefaultRowMapperFactory;
import com.github.rocketk.jorm.mapper.row.RowMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.github.rocketk.jorm.conf.ConfigFactory.defaultConfig;

/**
 * @author pengyu
 * @date 2022/4/24
 */
public class Transaction {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private StickyConnectionDataSourceWrapper ds;
    private TransactionalConnectionWrapper singleConnection;
    private boolean hasRollback;
    private final Config config;
    private RowMapperFactory rowMapperFactory;

    private Consumer<Transaction> operations;
    private BiConsumer<Transaction, Exception> onErrorFunc = (t, e) -> {
        logger.error(e.getMessage(), e);
        t.rollback();
    };

    public Transaction(DataSource ds) {
        this.ds = new StickyConnectionDataSourceWrapper(ds);
        try {
            singleConnection = (TransactionalConnectionWrapper) ds.getConnection();
        } catch (SQLException e) {
            throw new JormTransactionException(e);
        }
        this.config = defaultConfig();
        init();
    }

    public Transaction(DataSource ds, Config config) {
        this.ds = new StickyConnectionDataSourceWrapper(ds);
        try {
            singleConnection = (TransactionalConnectionWrapper) this.ds.getConnection();
        } catch (SQLException e) {
            throw new JormTransactionException(e);
        }
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
