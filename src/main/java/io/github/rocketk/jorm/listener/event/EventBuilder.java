package io.github.rocketk.jorm.listener.event;

import com.google.common.collect.Maps;
import io.github.rocketk.jorm.MutationMode;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

/**
 * @author pengyu
 * @date 2022/8/3
 */
public class EventBuilder<E extends Event> {
    private Map<String, Object> context;
    private String sql;
    private Object[] args;
    private Date startedAt;
    private Date completedAt;
    private Duration costs;
    private boolean success;
    private Throwable exception;
    private long affectedRows;
    private MutationMode mutationMode;
    private long retrievedRows;

    private Class<E> eventType;

    public EventBuilder(Class<E> eventType) {
        this.eventType = eventType;
    }

    public static <T extends Event> EventBuilder<T> builder(Class<T> eventType) {
        return new EventBuilder<>(eventType);
    }

    public EventBuilder<E> context(Map<String, Object> context) {
        this.context = context;
        return this;
    }

    public EventBuilder<E> sql(String sql) {
        this.sql = sql;
        return this;
    }

    public EventBuilder<E> args(Object[] args) {
        this.args = args;
        return this;
    }

    public EventBuilder<E> startedAt(Date startedAt) {
        this.startedAt = startedAt;
        return this;
    }

    public EventBuilder<E> completedAt(Date completedAt) {
        this.completedAt = completedAt;
        return this;
    }

    public EventBuilder<E> costs(Duration costs) {
        this.costs = costs;
        return this;
    }

    public EventBuilder<E> success(boolean success) {
        this.success = success;
        return this;
    }

    public EventBuilder<E> exception(Throwable exception) {
        this.exception = exception;
        return this;
    }

    public EventBuilder<E> affectedRows(long affectedRows) {
        this.affectedRows = affectedRows;
        return this;
    }

    public EventBuilder<E> mutationMode(MutationMode mutationMode) {
        this.mutationMode = mutationMode;
        return this;
    }

    public EventBuilder<E> retrievedRows(long retrievedRows) {
        this.retrievedRows = retrievedRows;
        return this;
    }

    public EventBuilder<E> addContext(String key, String value) {
        if (context == null) {
            context = Maps.newLinkedHashMap();
        }
        context.put(key, value);
        return this;
    }

    public E build() {
        final E event;
        try {
            event = eventType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (WithContextEvent.class.isAssignableFrom(eventType)) {
            WithContextEvent e = (WithContextEvent) event;
            e.setContext(context);
        }
        if (StatementExecutedEvent.class.isAssignableFrom(eventType)) {
            StatementExecutedEvent e = (StatementExecutedEvent) event;
            e.setSql(sql);
            e.setArgs(args);
            e.setException(exception);
            e.setStartedAt(startedAt);
            e.setCompletedAt(completedAt);
            e.setSuccess(success);
            e.setCosts(costs);
        }
        if (QueryStatementExecutedEvent.class.isAssignableFrom(eventType)) {
            QueryStatementExecutedEvent e = (QueryStatementExecutedEvent) event;
            e.setRetrievedRows(retrievedRows);
        }
        if (MutationStatementExecutedEvent.class.isAssignableFrom(eventType)) {
            MutationStatementExecutedEvent e = (MutationStatementExecutedEvent) event;
            e.setAffectedRows(affectedRows);
        }
        return event;
    }
}
