package io.github.rocketk.jorm.listener.event;

import java.util.Optional;

/**
 * @author pengyu
 * @date 2022/8/3
 */
public interface Event {
    Optional<Object> getContext(String key);
}
