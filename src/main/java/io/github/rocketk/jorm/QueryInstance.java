package io.github.rocketk.jorm;

import io.github.rocketk.jorm.conf.Config;
import io.github.rocketk.jorm.dialect.Dialect;
import io.github.rocketk.jorm.dialect.LimitOffsetAppender;
import io.github.rocketk.jorm.dialect.LimitOffsetAppenderFactory;
import io.github.rocketk.jorm.err.JormQueryException;
import io.github.rocketk.jorm.mapper.row.RowMapper;
import io.github.rocketk.jorm.mapper.row.RowMapperFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author pengyu
 * @date 2021/12/13
 */
public class QueryInstance<T> extends AbstractQueryInstance<T> implements Query<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Set<String> selectedColumns = Sets.newHashSet();
    private Set<String> omittedColumns = Sets.newHashSet();
    private String whereClause;
    protected Object[] args;
    //    private List<Object> whereClauseArgs = new ArrayList<>();
    private String orderByClause;
    private Long limit;
    private Long offset;
    private boolean count;

    private boolean findDeletedRows;

    public QueryInstance(DataSource ds, Config config, Class<T> model) {
        super(ds, config, model);
    }

    public QueryInstance(DataSource ds, Config config, Class<T> model, RowMapperFactory rowMapperFactory) {
        super(ds, config, model, rowMapperFactory);
    }

    //    public JormModelQueryInstance(DataSource ds) {
//        this.ds = ds;
//        this.config = defaultConfig();
//    }

//    @Override
//    public ModelQuery<T> model(Class<T> model) {
//        this.model = model;
//        return this;
//    }

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
        this.whereClause = whereClause;
//        if (args != null) {
//            if (this.whereClauseArgs == null) {
//                this.whereClauseArgs = new ArrayList<>();
//            }
//            this.whereClauseArgs.addAll(Arrays.asList(args));
//            this.whereClauseArgs.addAll()
//        }
        this.args = Optional.ofNullable(args).orElse(new Object[0]);
        return this;
    }

    @Override
    public Query<T> rawSql(String rawSql, Object... args) {
        this.rawSql = rawSql;
        this.args = args;
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
    public Optional<T> first() {
//        final long t0 = System.currentTimeMillis();
        init();
        this.limit = 1L;
        final String sql = this.buildQuerySql();
        if (this.config.isPrintSql()) {
            logger.info("exec sql: \"{}\", args: \"{}\"", sql, args);
        }
//        final long t1 = System.currentTimeMillis();
//        logger.info("buildQuerySql cost: {} ms", t1 - t0);
        try (final Connection conn = this.ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
//            final long t2 = System.currentTimeMillis();
//            logger.info("prepareStatement cost: {} ms", t2 - t1);
            setArgs(ps, args);
//            final long t3 = System.currentTimeMillis();
//            logger.info("setArgs cost: {} ms", t3 - t2);
            try (final ResultSet rs = ps.executeQuery()) {
//                final long t4 = System.currentTimeMillis();
//                logger.info("executeQuery cost: {} ms", t4 - t3);
                final T obj = parseResultSetToSingleObject(rs);
//                final long t5 = System.currentTimeMillis();
//                logger.info("parseResultSetToSingleObject cost: {} ms", t5 - t4);
                return Optional.ofNullable(obj);
            } catch (SQLException e) {
                throw e;
            }
        } catch (SQLException e) {
            logger.error("an error occurred while executing sql: \"{}\", args: \"{}\". error: {}, errorCode: {}, sqlState: {}",
                    sql, args, e.getMessage(), e.getErrorCode(), e.getSQLState());
            throw new JormQueryException(e);
        } finally {
//            final long cost = System.currentTimeMillis() - t0;
//            logger.info("total cost: {} ms", cost);
        }

    }

    @Override
    public long count() {
        init();
        this.limit = 1L;
        count = true;
        final String sql = this.buildQuerySql();
        if (this.config.isPrintSql()) {
            logger.info("exec sql: \"{}\", args: \"{}\"", sql, args);
        }
        try (final Connection conn = this.ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            setArgs(ps, args);
            try (final ResultSet rs = ps.executeQuery()) {
                return parseResultSetToLong(rs);
            } catch (SQLException e) {
                logger.error("failed to execute the sql: \"{}\", args: \"{}\". error: {}, errorCode: {}, sqlState: {}",
                        sql, args, e.getMessage(), e.getErrorCode(), e.getSQLState());
                throw e;
            }
        } catch (SQLException e) {
            logger.error("an error occurred while executing sql: \"{}\", args: \"{}\". error: {}, errorCode: {}, sqlState: {}",
                    sql, args, e.getMessage(), e.getErrorCode(), e.getSQLState());
            throw new JormQueryException(e);
        }

    }

    @Override
    public List<T> find() {
        init();
        final String sql = this.buildQuerySql();
        if (this.config.isPrintSql()) {
            logger.info("exec sql: \"{}\", args: \"{}\"", sql, args);
        }
        try (final Connection conn = this.ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            setArgs(ps, args);
            try (final ResultSet rs = ps.executeQuery()) {
                return parseResultSetToList(rs);
            } catch (SQLException e) {
                throw e;
            }
        } catch (SQLException e) {
            throw new JormQueryException(e);
        }
    }

//    private boolean onlyFindNonDeleted() {
//        if (this.findDeletedRows) {
//            return false;
//        }
//        return onlyFindNonDeletedByAnnotation(this.model);
//    }

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
        appendWhereClause(findDeletedRows, sql, whereClause);
        if (this.orderByClause != null) {
            sql.append(" order by ").append(orderByClause).append(" ");
        }
        appendLimitAndOffset(sql);
        return sql.toString();
    }

//    private void setValues(PreparedStatement ps, Object... args) {
//        if (args == null || args.length == 0) {
//            return;
//        }
//        try {
//            for (int i = 0; i < args.length; i++) {
//                final Object arg = args[i];
//                final int parameterIndex = i + 1;
//                if (arg == null) {
//                    ps.setNull(parameterIndex, ps.getParameterMetaData().getParameterType(parameterIndex));
//                } else if (arg instanceof Integer) {
//                    ps.setInt(parameterIndex, (Integer) arg);
//                } else {
//                    ps.setObject(parameterIndex, arg);
//                }
//            }
//        } catch (SQLException e) {
//            throw new JormQueryException(e);
//        }
//    }

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
        return this.selectedColumns != null && this.selectedColumns.size() > 0;
    }

    private void appendLimitAndOffset(StringBuilder sql) {
        LimitOffsetAppender limitOffsetAppender = config.getLimitOffsetAppender();
        if (limitOffsetAppender == null) {
            limitOffsetAppender = LimitOffsetAppenderFactory.byDialect(config.getDialect());
        }
//        if (limitOffsetAppender == null) {
//            throw new NullPointerException("limitOffsetAppender is null");
//        }
        limitOffsetAppender.appendLimitAndOffset(sql, limit, offset);
    }

}
