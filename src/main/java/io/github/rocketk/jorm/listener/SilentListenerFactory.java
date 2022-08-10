package io.github.rocketk.jorm.listener;

import io.github.rocketk.jorm.listener.event.Event;

/**
 * @author pengyu
 */
public class SilentListenerFactory {
    public static <E extends Event> Listener<E> silentListener(Class<E> eventType) {
        return event -> {
            // do nothing
        };
    }
}
