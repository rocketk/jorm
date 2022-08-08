package io.github.rocketk.jorm.listener.event;

import io.github.rocketk.jorm.MutationMode;

/**
 * @author pengyu
 * @date 2022/8/3
 */
public class MutationStatementExecutedEvent extends StatementExecutedEvent {

    protected long affectedRows;
    protected MutationMode mutationMode;

    public long getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(long affectedRows) {
        this.affectedRows = affectedRows;
    }

    public MutationMode getMutationMode() {
        return mutationMode;
    }

    public void setMutationMode(MutationMode mutationMode) {
        this.mutationMode = mutationMode;
    }
}
