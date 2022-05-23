package io.github.rocketk.jorm.mapper.row;

/**
 * @author pengyu
 */
@FunctionalInterface
public interface RowMapperFactory {
    <T> RowMapper<T> getRowMapper(Class<T> model);
}
