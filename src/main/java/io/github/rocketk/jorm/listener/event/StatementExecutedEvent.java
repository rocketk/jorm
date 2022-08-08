package io.github.rocketk.jorm.listener.event;

import java.time.Duration;
import java.util.Date;

/**
 * @author pengyu
 * @date 2022/8/3
 */
public abstract class StatementExecutedEvent extends WithContextEvent {
    protected String sql;
    protected Object[] args;
    protected Date startedAt;
    protected Date completedAt;
    protected Duration costs;
    protected boolean success;
    protected Throwable exception;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Duration getCosts() {
        return costs;
    }

    public void setCosts(Duration costs) {
        this.costs = costs;
    }
}
