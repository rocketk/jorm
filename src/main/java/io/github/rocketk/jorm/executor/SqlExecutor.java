package io.github.rocketk.jorm.executor;

/**
 * @author pengyu
 */
public interface SqlExecutor {
    <T> T executeQuery(SqlRequest sqlRequest, ResultSetReader<T> resultSetReader);
    long[] executeUpdateAndReturnKeys(SqlRequest sqlRequest);
    long executeUpdate(SqlRequest sqlRequest);
}
