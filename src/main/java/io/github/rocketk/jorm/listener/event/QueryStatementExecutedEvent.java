package io.github.rocketk.jorm.listener.event;

/**
 * @author pengyu
 * @date 2022/8/3
 */
public class QueryStatementExecutedEvent extends StatementExecutedEvent {
    protected long retrievedRows;

    public long getRetrievedRows() {
        return retrievedRows;
    }

    public void setRetrievedRows(long retrievedRows) {
        this.retrievedRows = retrievedRows;
    }
}
