package io.github.rocketk.jorm.listener.event;

import com.google.common.collect.Maps;
import io.github.rocketk.jorm.executor.StmtType;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

/**
 * @author pengyu
 */
public class EventBuilder<E> {
    private Map<String, Object> context;
    private String instanceName;
    private StmtType stmtType;
    private String sql;
    private Object[] args;
    private Date startedAt;
    private Date completedAt;
    private boolean success;
    private Throwable exception;
    private String operationId;

    private Class<E> eventType;

    public EventBuilder(Class<E> eventType) {
        this.eventType = eventType;
    }

    public static <T extends Event> EventBuilder<T> builder(Class<T> eventType) {
        return new EventBuilder<>(eventType);
    }

    public EventBuilder<E> instanceName(String instanceName) {
        this.instanceName = instanceName;
        return this;
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

    public EventBuilder<E> success(boolean success) {
        this.success = success;
        return this;
    }

    public EventBuilder<E> exception(Throwable exception) {
        this.exception = exception;
        return this;
    }

    public EventBuilder<E> operationId(String operationId) {
        this.operationId = operationId;
        return this;
    }

    public EventBuilder<E> addContext(String key, String value) {
        if (context == null) {
            context = Maps.newLinkedHashMap();
        }
        context.put(key, value);
        return this;
    }

    public EventBuilder<E> eventType(Class<E> eventType) {
        this.eventType = eventType;
        return this;
    }

    public EventBuilder<E> stmtType(StmtType stmtType) {
        this.stmtType = stmtType;
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
            e.setContext(context == null ? Maps.newLinkedHashMap() : context);
        }
        if (StatementExecutedEvent.class.isAssignableFrom(eventType)) {
            StatementExecutedEvent e = (StatementExecutedEvent) event;
            e.setInstanceName(instanceName);
            e.setStmtType(stmtType);
            e.setSql(sql);
            e.setArgs(args);
            e.setException(exception);
            e.setStartedAt(startedAt);
            e.setCompletedAt(completedAt);
            e.setSuccess(success);
            e.setOperationId(operationId);
            e.setCosts(Duration.ofMillis(completedAt.getTime() - startedAt.getTime()));
        }
        return event;
    }

}
