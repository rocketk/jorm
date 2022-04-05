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

    Update<T> value(String column, Object value);

    Update<T> values(Map<String, Object> valuesMap);

    boolean execInsert();
    long execInsertAndReturnFirstKey();
    long[] execInsertAndReturnKeys();

    boolean execUpdate();
}
