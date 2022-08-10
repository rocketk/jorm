package io.github.rocketk.jorm.listener.event;

import java.time.Duration;
import java.util.Date;

/**
 * @author pengyu
 */
public class StatementExecutedEvent extends WithContextEvent {
    private StmtType stmtType;
    private String sql;
    private Object[] args;
    private Date startedAt;
    private Date completedAt;
    private Duration costs;
    private boolean success;
    private Throwable exception;

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

    public StmtType getStmtType() {
        return stmtType;
    }

    public void setStmtType(StmtType stmtType) {
        this.stmtType = stmtType;
    }

    public static enum StmtType {
        QUERY,
        MUTATION
    }
}
