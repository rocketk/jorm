package com.github.rocketk.jorm;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author pengyu
 * @date 2022/3/28
 */
public interface Update<T> {

    Update<T> table(String table);

    Update<T> omit(String... columns);

    Update<T> obj(@Nullable T obj);

    Update<T> set(String column, Object value);

    Update<T> set(Map<String, Object> valuesMap);

    Update<T> where(String whereClause, Object... args);

    Update<T> ignoreNoWhereClauseWarning(boolean ignoreNoWhereClauseWarning);

    Update<T> shouldUpdateDeletedRows(boolean updateDeleted);

    boolean execInsert();

    long execInsertAndReturnFirstKey();

    long[] execInsertAndReturnKeys();

    long execUpdate();

    long execDelete();
}
