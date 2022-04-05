package com.github.rocketk.jorm;

import com.github.rocketk.jorm.conf.Config;
import com.github.rocketk.jorm.dialect.Dialect;
import com.github.rocketk.jorm.mapper.row.RowMapper;
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

import static com.github.rocketk.jorm.ReflectionUtil.deletedAtColumn;
import static com.github.rocketk.jorm.ReflectionUtil.onlyFindNonDeletedByAnnotation;

/**
 * @author pengyu
 * @date 2021/12/13
 */
public class QueryInstance<T> extends AbstractQueryInstance<T> implements Query<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Set<String> selectedColumns = Sets.newHashSet();
    private Set<String> omittedColumns = Sets.newHashSet();
    private String whereClause;
    //    private List<Object> whereClauseArgs = new ArrayList<>();
    private String orderByClause;
    private Integer limit;
    private Integer offset;
    private boolean count;

    private boolean findDeleted;

    public QueryInstance(DataSource ds, Config config, Class<T> model) {
        super(ds, config, model);
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
    public Query<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public Query<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public Query<T> dialect(Dialect dialect) {
        this.dialect = dialect;
        return this;
    }

    @Override
    public Query<T> shouldFindDeleted(boolean findDeleted) {
        this.findDeleted = findDeleted;
        return this;
    }

    @Override
    public Optional<T> first() {
        init();
        this.limit = 1;
        final String sql = this.buildQuerySql();
        if (this.config.isPrintSql()) {
            logger.info("exec sql: \"{}\", args: \"{}\"", sql, args);
        }
        try (final Connection conn = this.ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            setArgs(ps, args);
            try (final ResultSet rs = ps.executeQuery()) {
                final T obj = parseResultSetToSingleObject(rs);
                return Optional.ofNullable(obj);
            } catch (SQLException e) {
                throw e;
            }
        } catch (SQLException e) {
            logger.error("an error occurred while executing sql: \"{}\", args: \"{}\". error: {}, errorCode: {}, sqlState: {}",
                    sql, args, e.getMessage(), e.getErrorCode(), e.getSQLState());
            throw new JormQueryException(e);
        }

    }

    @Override
    public long count() {
        init();
        this.limit = 1;
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

    private boolean onlyFindNonDeleted() {
        if (this.findDeleted) {
            return false;
        }
        return onlyFindNonDeletedByAnnotation(this.model);
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
        final boolean _onlyFindNonDeleted = onlyFindNonDeleted();
        if (_onlyFindNonDeleted) {
            sql.append(" where ").append(deletedAtColumn(this.model)).append(" is null").append(" ");
        }
        if (this.whereClause != null) {
            if (_onlyFindNonDeleted) {
                sql.append(" and ");
            } else {
                sql.append(" where ");
            }
            sql.append(whereClause).append(" ");
        }
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
        if (Dialect.STANDARD.equals(this.dialect)) {
            if (this.limit != null) {
                sql.append(" limit ").append(this.limit).append(" ");
            }
            if (this.offset != null) {
                sql.append(" offset ").append(this.offset).append(" ");
            }
            return;
        }
        if (offset != null) {
            sql.append(" offset ").append(offset);
        }
        if (limit != null) {
            sql.append(" fetch first ").append(limit).append(" rows only");
        }
    }
}
