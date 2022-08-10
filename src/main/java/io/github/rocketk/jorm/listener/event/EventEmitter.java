package io.github.rocketk.jorm.listener.event;

import io.github.rocketk.jorm.listener.Listener;

import java.util.Date;

/**
 * @author pengyu
 */
public class EventEmitter<E extends Event> {
    private EventBuilder<E> eventBuilder;
    private Listener<E> listener;

    public EventEmitter() {
    }

    public EventEmitter(EventBuilder<E> eventBuilder, Listener<E> listener) {
        this.eventBuilder = eventBuilder;
        this.listener = listener;
    }

    public EventBuilder<E> getEventBuilder() {
        return eventBuilder;
    }

    public void setEventBuilder(EventBuilder<E> eventBuilder) {
        this.eventBuilder = eventBuilder;
    }

    public void emit() {
        this.emit(null);
    }

    public void emit(Throwable e) {
        if (eventBuilder == null || listener == null) {
            return;
        }
        listener.onEvent(eventBuilder.completedAt(new Date()).success(e != null).build());
    }
}
