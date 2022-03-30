package com.github.rocketk.jorm.mapper.row;

/**
 * @author pengyu
 * @date 2021/12/16
 */
@FunctionalInterface
public interface RowMapperFactory {
    <T> RowMapper<T> createRowMapper(Class<T> model);
}
