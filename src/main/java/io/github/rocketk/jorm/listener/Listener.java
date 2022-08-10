package io.github.rocketk.jorm.listener;

import io.github.rocketk.jorm.listener.event.Event;

/**
 * @author pengyu
 */
@FunctionalInterface
public interface Listener<E extends Event> {
    void onEvent(E event);
}
