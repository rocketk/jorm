package com.github.rocketk.jorm;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author pengyu
 * @date 2022/3/28
 */
public interface ModelUpdate<T> {

    ModelUpdate<T> table(String table);

    ModelUpdate<T> obj(@Nullable T obj);

    ModelUpdate<T> value(String column, Object value);

    ModelUpdate<T> values(Map<String, Object> valuesMap);

    boolean execInsert();
    long execInsertAndReturnFirstKey();
    long[] execInsertAndReturnKeys();

    boolean execUpdate();
}
