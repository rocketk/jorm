package io.github.rocketk.jorm;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author pengyu
 */
public interface Mutation<T> {

    Mutation<T> table(String table);

    Mutation<T> omit(String... columns);

    Mutation<T> obj(@Nullable T obj);

    Mutation<T> set(String column, Object value);

    Mutation<T> set(Map<String, Object> valuesMap);

    Mutation<T> where(String whereClause, Object... args);

    Mutation<T> ignoreNoWhereClauseWarning(boolean ignoreNoWhereClauseWarning);

    Mutation<T> shouldUpdateDeletedRows(boolean updateDeleted);

    Mutation<T> operationId(String operationId);

    boolean insert();

    long insertAndReturnFirstKey();

    long[] insertAndReturnKeys();

    long update();

    long delete();
}
