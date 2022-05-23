package io.github.rocketk.jorm.dialect;

/**
 * @author pengyu
 */
@FunctionalInterface
public interface LimitOffsetAppender {
    void appendLimitAndOffset(StringBuilder sql, Long limit, Long offset);
}
