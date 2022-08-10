package io.github.rocketk.jorm.executor;

import java.sql.PreparedStatement;

/**
 * @author pengyu
 */
@FunctionalInterface
public interface ArgumentsSetter {
    void setArguments(PreparedStatement ps, Object[] arguments);
}
