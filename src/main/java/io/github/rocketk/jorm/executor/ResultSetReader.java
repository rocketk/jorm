package io.github.rocketk.jorm.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author pengyu
 * @date 2022/8/8
 */
@FunctionalInterface
public interface ResultSetReader<T> {
    T read(ResultSet rs) throws SQLException;
}
