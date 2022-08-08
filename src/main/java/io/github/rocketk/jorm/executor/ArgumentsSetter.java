package io.github.rocketk.jorm.executor;

import java.sql.PreparedStatement;

/**
 * @author pengyu
 * @date 2022/8/8
 */
@FunctionalInterface
public interface ArgumentsSetter {
    void setArguments(PreparedStatement ps, Object[] arguments);
}
