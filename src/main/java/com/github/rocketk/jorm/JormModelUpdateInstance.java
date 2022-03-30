package com.github.rocketk.jorm;

import com.github.rocketk.jorm.conf.Config;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.rocketk.jorm.ReflectionUtil.columnName;
import static com.github.rocketk.jorm.ReflectionUtil.shouldIgnoreWriteToDb;

/**
 * @author pengyu
 * @date 2022/3/28
 */
public class JormModelUpdateInstance<T> extends AbstractModelQueryInstance<T> implements ModelUpdate<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    //    private Map<String, Object> args = new HashMap<>();
    private List<String> argKeys = Lists.newArrayList();
    private List<Object> argValues = Lists.newArrayList();

    public JormModelUpdateInstance(DataSource ds, Config config, Class<T> model) {
        super(ds, config, model);
    }

    @Override
    public ModelUpdate<T> table(String table) {
        this.table = table;
        return this;
    }

    @Override
    public ModelUpdate<T> obj(T obj) {
        assert obj != null;
        this.model = (Class<T>) obj.getClass();
        final Field[] fields = this.model.getDeclaredFields();
        for (Field f : fields) {
            if (shouldIgnoreWriteToDb(f)) {
                continue;
            }
            String columnName = columnName(f);
            if (columnName == null || columnName.isEmpty()) {
                columnName = this.columnFieldNameMapper.fieldNameToColumnName(f.getName());
            }
            try {
                final Object value = f.get(obj);
                this.argKeys.add(columnName);
                this.argValues.add(value);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
                throw new JormQueryException(e);
            }
        }
        return this;
    }

    @Override
    public ModelUpdate<T> value(String column, Object value) {
//        this.args.put(column, value);
        this.argKeys.add(column);
        this.argValues.add(value);
        return this;
    }

    @Override
    public ModelUpdate<T> values(Map<String, Object> valuesMap) {
//        this.args.putAll(valuesMap);
        Optional.ofNullable(valuesMap).ifPresent(m -> m.forEach(this::value));
//        if (valuesMap != null && valuesMap.size() > 0) {
//            valuesMap.forEach(this::value);
//        }
        return this;
    }

    @Override
    public boolean execInsert() {
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
            throw new JormQueryException(e);
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
    public boolean execUpdate() {
        return false;
    }

    private String buildInsertSql() {
        final StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(this.table).append(" ");
        if (this.argKeys.size() == 0) {
            throw new JormQueryException("argKeys is empty while building the insertion sql");
        }
        sql.append("set ");
//        this.args.entrySet().forEach((e) -> {
//            sql.append(e.getKey()).append("=?,");
//        });
        this.argKeys.forEach(key -> sql.append(key).append("=?,"));
        sql.deleteCharAt(sql.length() - 1);
        return sql.toString();
    }

}
