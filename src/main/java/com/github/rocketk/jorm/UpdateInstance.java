package com.github.rocketk.jorm;

import com.github.rocketk.jorm.conf.Config;
import com.github.rocketk.jorm.mapper.row.RowMapperFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Date;
import java.util.*;

import static com.github.rocketk.jorm.util.ReflectionUtil.*;

/**
 * @author pengyu
 * @date 2022/3/28
 */
public class UpdateInstance<T> extends AbstractQueryInstance<T> implements Update<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    //    private Map<String, Object> args = new HashMap<>();
    private final int MODE_INSERT = 0;
    private final int MODE_UPDATE = 1;
    private int mode; // 0 for insert, 1 for update
    private final List<String> argKeys = Lists.newArrayList();
    private final List<Object> argValues = Lists.newArrayList();
    private String whereClause;
    private Object[] whereArgs;
    private boolean ignoreNoWhereClauseWarning;
    private boolean updateDeletedRows;
    private final Map<String, Object> argsMap = Maps.newLinkedHashMap();
    private final List<String> omittedColumns = Lists.newArrayList();
    private T object;

    public UpdateInstance(DataSource ds, Config config, Class<T> model) {
        super(ds, config, model);
    }

    public UpdateInstance(DataSource ds, Config config, Class<T> model, RowMapperFactory rowMapperFactory) {
        super(ds, config, model, rowMapperFactory);
    }

    @Override
    protected void init() {
        super.init();
        initArgs();
    }

    private void initArgs() {
        initObject();
        autoGenerateDateColumns();
        omittedColumns.forEach(argsMap::remove);
        argsMap.forEach((k, v) -> {
            argKeys.add(k);
            argValues.add(v);
        });
        // only for updating
        if (mode == MODE_UPDATE && whereArgs != null && whereArgs.length > 0) {
            argValues.addAll(Arrays.asList(whereArgs));
        }
    }

    private void autoGenerateDateColumns() {
        final Class<T> type;
        if (model != null) {
            type = model;
        } else if (object != null) {
            type = (Class<T>) object.getClass();
        } else {
            return;
        }
        switch (mode) {
            case MODE_INSERT:
                createdAtColumn(type).ifPresent(column -> argsMap.putIfAbsent(column, new Date()));
                // pass
            case MODE_UPDATE:
                updatedAtColumn(type).ifPresent(column -> argsMap.putIfAbsent(column, new Date()));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private void initObject() {
        if (this.object == null) {
            return;
        }
//        setupCreatedAt(this.object);
//        setupUpdatedAt(this.object);
        final Field[] fields = this.model.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (shouldIgnoreWriteToDb(f)) {
                continue;
            }
//            String columnName = columnName(f);
//            if (StringUtils.isBlank(columnName)) {
//                columnName = this.columnFieldNameMapper.fieldNameToColumnName(f.getName());
//            }
            final String columnName = columnName(f).orElseGet(() -> columnFieldNameMapper.fieldNameToColumnName(f.getName()));
            if (omittedColumns.contains(columnName)) {
                continue;
            }
            try {
                final Object value = f.get(this.object);
//                this.argKeys.add(columnName);
//                this.argValues.add(value);
                // 使用 obj() 添加的待更新列，是不考虑 null 值的，如果希望强制设置 null 值给数据库，应当使用 set() 方法
                if (value != null) {
                    this.argsMap.putIfAbsent(columnName, value);
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
                throw new JormUpdateException(e);
            }
        }
    }

    @Override
    public Update<T> table(String table) {
        this.table = table;
        return this;
    }

    @Override
    public Update<T> omit(String... columns) {
        omittedColumns.addAll(Arrays.asList(columns));
        return this;
    }

    @Override
    public Update<T> obj(T obj) {
        this.model = (Class<T>) obj.getClass();
        this.object = obj;
        return this;
    }

    @Override
    public Update<T> set(String column, Object value) {
//        this.args.put(column, value);
//        this.argKeys.add(column);
//        this.argValues.add(value);
        this.argsMap.put(column, value);
        return this;
    }

    @Override
    public Update<T> set(Map<String, Object> valuesMap) {
//        this.args.putAll(valuesMap);
        Optional.ofNullable(valuesMap).ifPresent(m -> m.forEach(this::set));
//        if (valuesMap != null && valuesMap.size() > 0) {
//            valuesMap.forEach(this::value);
//        }
        return this;
    }

    @Override
    public Update<T> where(String whereClause, Object... args) {
        this.whereClause = whereClause;
        this.whereArgs = args;
        return this;
    }

    @Override
    public Update<T> ignoreNoWhereClauseWarning(boolean ignoreNoWhereClauseWarning) {
        this.ignoreNoWhereClauseWarning = ignoreNoWhereClauseWarning;
        return this;
    }

    @Override
    public Update<T> shouldUpdateDeletedRows(boolean updateDeleted) {
        this.updateDeletedRows = updateDeleted;
        return this;
    }

    @Override
    public boolean execInsert() {
        mode = MODE_INSERT;
        init();
        final String sql = this.buildInsertSql();
        if (this.config.isPrintSql()) {
            logger.info("exec sql: \"{}\", argValues: \"{}\"", sql, this.argValues);
        }
        try (final Connection conn = this.ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            setArgs(ps, this.argValues.toArray());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new JormUpdateException(e);
        }
    }

    @Override
    public long execInsertAndReturnFirstKey() {
        final long[] keys = this.execInsertAndReturnKeys();
        if (keys.length == 0) {
            throw new JormUpdateException("at last 1 generated key is expected");
        }
        return keys[0];
    }

    @Override
    public long[] execInsertAndReturnKeys() {
        mode = MODE_INSERT;
        init();
        final String sql = this.buildInsertSql();
        if (this.config.isPrintSql()) {
            logger.info("exec sql: \"{}\", argValues: \"{}\"", sql, this.argValues);
        }
        try (final Connection conn = this.ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setArgs(ps, this.argValues.toArray());
            final int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new JormUpdateException("failed to insert: " + this.table);
            }
            try (final ResultSet generatedKeys = ps.getGeneratedKeys()) {
                List<Long> keys = Lists.newArrayList();
                while (generatedKeys.next()) {
                    keys.add(generatedKeys.getLong(1));
                }
                return keys.stream().mapToLong(Long::longValue).toArray();
            }
        } catch (SQLException e) {
            throw new JormUpdateException(e);
        }
    }

    @Override
    public long execUpdate() {
        mode = MODE_UPDATE;
//        throw new UnsupportedOperationException("method execUpdate is not implemented");
        init();
        final String sql = buildUpdateSql();
        if (config.isPrintSql()) {
            logger.info("exec sql: \"{}\", argValues: \"{}\"", sql, argValues);
        }
        try (final Connection conn = this.ds.getConnection();
             final PreparedStatement ps = conn.prepareStatement(sql)) {
            setArgs(ps, argValues.toArray());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new JormUpdateException(e);
        }
    }

    private String buildInsertSql() {
        if (this.argKeys.size() == 0) {
            throw new JormUpdateException("argKeys is empty while building the insertion sql");
        }
        final StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(this.table).append(" (");
        argKeys.forEach(key -> sql.append(key).append(","));
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" ) values ( ");
        argKeys.forEach(v -> sql.append("?,"));
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" )");
        return sql.toString();
    }

    private String buildUpdateSql() {
        if (this.argKeys.size() == 0) {
            throw new JormUpdateException("argKeys is empty while building the insert sql");
        }
        if (!this.ignoreNoWhereClauseWarning && StringUtils.isBlank(this.whereClause)) {
            throw new JormUpdateException("where clause is empty while building the update sql, please call ignoreNoWhereClauseWarning(true) if you don't want to see this");
        }
        final StringBuilder sql = new StringBuilder();
        sql.append("update ").append(this.table).append(" set ");
        this.argKeys.forEach(key -> sql.append(key).append("=?,"));
        sql.deleteCharAt(sql.length() - 1);
        // where?
        appendWhereClause(updateDeletedRows, sql, whereClause);
        return sql.toString();
    }

}
