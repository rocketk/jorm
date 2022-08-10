package io.github.rocketk.jorm.listener;

/**
 * @author pengyu
 */
@FunctionalInterface
public interface SqlTagMapper {
    String toSql(String rawSql, Object[] args);
}
