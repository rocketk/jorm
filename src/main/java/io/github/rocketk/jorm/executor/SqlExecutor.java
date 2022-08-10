package io.github.rocketk.jorm.executor;

import javax.sql.DataSource;

/**
 * @author pengyu
 */
public interface SqlExecutor {
    <T> T executeQuery(DataSource ds, String sql, Object[] args, ArgumentsSetter argsSetter, ResultSetReader<T> resultSetReader);
    long[] executeUpdateAndReturnKeys(DataSource ds, String sql, Object[] args, ArgumentsSetter argsSetter);
    long executeUpdate(DataSource ds, String sql, Object[] args, ArgumentsSetter argsSetter);
}
