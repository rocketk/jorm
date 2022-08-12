package io.github.rocketk.jorm.executor;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * @author pengyu
 */
public class SqlRequest {
    private String instanceName;
    private String operationId;
    private String sql;
    private Object[] args;
    private StmtType stmtType;
    private DataSource dataSource;
    private ArgumentsSetter argsSetter;

    public SqlRequest(String instanceName, String operationId, String sql, Object[] args, StmtType stmtType, DataSource dataSource, ArgumentsSetter argsSetter) {
        this.instanceName = instanceName;
        this.operationId = operationId;
        this.sql = sql;
        this.args = args;
        this.stmtType = stmtType;
        this.dataSource = dataSource;
        this.argsSetter = argsSetter;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getOperationId() {
        return operationId;
    }

    public String getSql() {
        return sql;
    }

    public Object[] getArgs() {
        return args;
    }

    public StmtType getStmtType() {
        return stmtType;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public ArgumentsSetter getArgsSetter() {
        return argsSetter;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SqlRequest.class.getSimpleName() + "[", "]")
                .add("instanceName='" + instanceName + "'")
                .add("operationId='" + operationId + "'")
                .add("sql='" + sql + "'")
                .add("args=" + Arrays.toString(args))
                .add("stmtType=" + stmtType)
                .add("dataSource=" + dataSource)
                .add("argsSetter=" + argsSetter)
                .toString();
    }
}
