package io.github.rocketk.jorm.listener.event;

import java.util.Optional;

/**
 * @author pengyu
 */
public interface Event {
    Optional<Object> getContext(String key);
}
