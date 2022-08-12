package io.github.rocketk.jorm;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.rocketk.jorm.conf.Config;
import io.github.rocketk.jorm.dialect.Dialect;
import io.github.rocketk.jorm.dialect.LimitOffsetAppender;
import io.github.rocketk.jorm.dialect.LimitOffsetAppenderFactory;
import io.github.rocketk.jorm.executor.SqlExecutor;
import io.github.rocketk.jorm.executor.SqlRequest;
import io.github.rocketk.jorm.executor.SqlRequestBuilder;
import io.github.rocketk.jorm.executor.StmtType;
import io.github.rocketk.jorm.mapper.row.RowMapper;
import io.github.rocketk.jorm.mapper.row.RowMapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author pengyu
 */
public class QueryInstance<T> extends AbstractQueryInstance<T> implements Query<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected List<Object> args = Lists.newArrayList();
    private final Set<String> selectedColumns = Sets.newHashSet();
    private final Set<String> omittedColumns = Sets.newHashSet();
    //    private final SqlExecutor<T> sqlExecutor = new DefaultSqlExecutor<>();
    private StringBuilder whereClause = new StringBuilder(128);
    //    private List<Object> whereClauseArgs = new ArrayList<>();
    private String orderByClause;
    private Long limit;
    private Long offset;

    private boolean findDeletedRows;

    public QueryInstance(String jormInstanceName, DataSource ds, Config config, Class<T> model) {
        super(jormInstanceName, ds, config, model);
    }

    public QueryInstance(String jormInstanceName, DataSource ds, Config config, Class<T> model, RowMapperFactory rowMapperFactory) {
        super(jormInstanceName, ds, config, model, rowMapperFactory);
    }

    public QueryInstance(String jormInstanceName, DataSource ds, Config config, Class<T> model, RowMapperFactory rowMapperFactory, SqlExecutor sqlExecutor) {
        super(jormInstanceName, ds, config, model, rowMapperFactory, sqlExecutor);
    }

    @Override
    public Query<T> rowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
        return this;
    }

    @Override
    public Query<T> select(String... columns) {
        if (columns == null) {
            return this;
        }
        this.selectedColumns.addAll(Arrays.asList(columns));
        return this;
    }

    @Override
    public Query<T> omit(String... columns) {
        if (columns == null) {
            return this;
        }
        this.omittedColumns.addAll(Arrays.asList(columns));
        return this;
    }

    @Override
    public Query<T> table(String table) {
        this.table = table;
        return this;
    }

    @Override
    public Query<T> where(String whereClause, Object... args) {
        if (this.whereClause.length() > 0) {
            this.whereClause = new StringBuilder(128);
        }
        this.whereClause.append(whereClause);
        this.args = Lists.newArrayList();
        if (args != null && args.length > 0) {
            this.args.addAll(Arrays.asList(args));
        }
        return this;
    }

    @Override
    public Query<T> and(String whereClause, Object... args) {
        if (this.whereClause.length() > 0) {
            this.whereClause.append(" and (").append(whereClause).append(")");
        } else {
            this.whereClause.append(whereClause);
        }
        if (this.args == null) {
            this.args = Lists.newArrayList(16);
        }
        if (args != null && args.length > 0) {
            this.args.addAll(Arrays.asList(args));
        }
        return this;
    }

    @Override
    public Query<T> rawSql(String rawSql, Object... args) {
        this.rawSql = rawSql;
        this.args = Arrays.asList(args);
        return this;
    }

    @Override
    public Query<T> orderBy(String orderByClause) {
        this.orderByClause = orderByClause;
        return this;
    }

    @Override
    public Query<T> limit(long limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public Query<T> offset(long offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public Query<T> dialect(Dialect dialect) {
        this.config.setDialect(dialect);
        return this;
    }

    @Override
    public Query<T> shouldFindDeletedRows(boolean findDeleted) {
        this.findDeletedRows = findDeleted;
        return this;
    }

    @Override
    public Query<T> operationId(String operationId) {
        super.operationId = operationId;
        return this;
    }

    private SqlRequest createSqlRequest() {
        return SqlRequestBuilder.builder()
                .instanceName(jormInstanceName)
                .operationId(operationId)
                .dataSource(ds)
                .sql(this.buildQuerySql())
                .args(args.toArray())
                .argsSetter(this::setArgs)
                .stmtType(StmtType.QUERY)
                .build();
    }
    @Override
    public Optional<T> first() {
//        final long t0 = System.currentTimeMillis();
        init();
        this.limit = 1L;
        final String sql = this.buildQuerySql();
//        return sqlExecutor.executeQuery(ds, sql, args.toArray(), this::setArgs,
//                rs -> Optional.ofNullable(parseResultSetToSingleObject(rs)),
//                operationId);
        return sqlExecutor.executeQuery(createSqlRequest(), rs -> Optional.ofNullable(parseResultSetToSingleObject(rs)));
    }

    @Override
    public long count() {
        init();
        this.limit = 1L;
        count = true;

//        return sqlExecutor.executeQuery(ds, sql, args.toArray(), this::setArgs, this::parseResultSetToLong, operationId);
        return sqlExecutor.executeQuery(createSqlRequest(), this::parseResultSetToLong);
    }

    @Override
    public List<T> find() {
        init();
//        return sqlExecutor.executeQuery(ds, sql, args.toArray(), this::setArgs, this::parseResultSetToList, operationId);
        return sqlExecutor.executeQuery(createSqlRequest(), this::parseResultSetToList);
    }

    private String buildQuerySql() {
        if (StringUtils.isNotBlank(this.rawSql)) {
            return this.rawSql;
        }
        final StringBuilder sql = new StringBuilder();
        sql.append("select ");
        if (this.count) {
            sql.append(" count(*) ");
        } else {
            sql.append(this.hasSelectedColumns() ? String.join(",", this.selectedColumns) : "*");
        }
        sql.append(" from ").append(this.table).append(" ");
        // where?
        appendWhereClause(findDeletedRows, sql, whereClause.toString());
        if (this.orderByClause != null) {
            sql.append(" order by ").append(orderByClause).append(" ");
        }
        appendLimitAndOffset(sql);
        return sql.toString();
    }

    private List<T> parseResultSetToList(ResultSet rs) throws SQLException {
        final List<T> list = Lists.newArrayList();
        while (rs.next()) {
            final T obj = rowMapper.rowToModel(rs, this.omittedColumns);
            list.add(obj);
        }
        return list;
    }

    private T parseResultSetToSingleObject(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return rowMapper.rowToModel(rs, this.omittedColumns);
        }
        return null;
    }

    private long parseResultSetToLong(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return rs.getLong(1);
        }
        // error
        return -1;
    }

    private boolean hasSelectedColumns() {
        return this.selectedColumns.size() > 0;
    }

    private void appendLimitAndOffset(StringBuilder sql) {
        LimitOffsetAppender limitOffsetAppender = config.getLimitOffsetAppender();
        if (limitOffsetAppender == null) {
            limitOffsetAppender = LimitOffsetAppenderFactory.byDialect(config.getDialect());
        }
        limitOffsetAppender.appendLimitAndOffset(sql, limit, offset);
    }

}
