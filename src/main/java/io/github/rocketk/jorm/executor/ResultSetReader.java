package io.github.rocketk.jorm.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author pengyu
 */
@FunctionalInterface
public interface ResultSetReader<T> {
    T read(ResultSet rs) throws SQLException;
}
