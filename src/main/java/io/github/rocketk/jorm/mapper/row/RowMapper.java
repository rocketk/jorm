package io.github.rocketk.jorm.mapper.row;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author pengyu
 */
@FunctionalInterface
public interface RowMapper<T> {
    T rowToModel(ResultSet rs, Set<String> omittedColumns) throws SQLException;
}
