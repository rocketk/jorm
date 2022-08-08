package io.github.rocketk.jorm.listener;

/**
 * @author pengyu
 * @date 2022/8/3
 */
@FunctionalInterface
public interface SqlMapper {
    String toSql(String rawSql, Object[] args);
}
