package io.github.rocketk.jorm.executor;

import javax.sql.DataSource;

/**
 * @author pengyu
 */
public class SqlRequestBuilder {
    private String instanceName;
    private String operationId;
    private String sql;
    private Object[] args;
    private StmtType stmtType;
    private DataSource dataSource;
    private ArgumentsSetter argsSetter;

    public static SqlRequestBuilder builder() {
        return new SqlRequestBuilder();
    }

    public SqlRequestBuilder instanceName(String instanceName) {
        this.instanceName = instanceName;
        return this;
    }

    public SqlRequestBuilder operationId(String operationId) {
        this.operationId = operationId;
        return this;
    }

    public SqlRequestBuilder sql(String sql) {
        this.sql = sql;
        return this;
    }

    public SqlRequestBuilder args(Object[] args) {
        this.args = args;
        return this;
    }

    public SqlRequestBuilder stmtType(StmtType stmtType) {
        this.stmtType = stmtType;
        return this;
    }

    public SqlRequestBuilder dataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public SqlRequestBuilder argsSetter(ArgumentsSetter argsSetter) {
        this.argsSetter = argsSetter;
        return this;
    }

    public SqlRequest build() {
        return new SqlRequest(instanceName, operationId, sql, args, stmtType, dataSource, argsSetter);
    }
}
