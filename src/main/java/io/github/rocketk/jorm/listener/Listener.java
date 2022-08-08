package io.github.rocketk.jorm.listener;

import io.github.rocketk.jorm.listener.event.Event;

/**
 * @author pengyu
 * @date 2022/8/3
 */
public interface Listener<E extends Event> {
    void onEvent(E event);
}
