package com.github.rocketk.jorm.dialect;

/**
 * @author pengyu
 * @date 2022/4/23
 */
@FunctionalInterface
public interface LimitOffsetAppender {
    void appendLimitAndOffset(StringBuilder sql, Long limit, Long offset );
}
