package io.github.rocketk.jorm;

import com.google.common.collect.Maps;
import io.github.rocketk.jorm.anno.JormCustomEnum;
import io.github.rocketk.jorm.conf.Config;
import io.github.rocketk.jorm.err.JormQueryException;
import io.github.rocketk.jorm.executor.DefaultSqlExecutor;
import io.github.rocketk.jorm.executor.SqlExecutor;
import io.github.rocketk.jorm.json.JsonMapper;
import io.github.rocketk.jorm.json.JsonMapperFactory;
import io.github.rocketk.jorm.mapper.column.ColumnFieldNameMapper;
import io.github.rocketk.jorm.mapper.column.DelimiterBasedStringArrayColumnFieldMapper;
import io.github.rocketk.jorm.mapper.column.SnakeCamelColumnFieldNameMapper;
import io.github.rocketk.jorm.mapper.column.StringArrayColumnFieldMapper;
import io.github.rocketk.jorm.mapper.row.DefaultRowMapperFactory;
import io.github.rocketk.jorm.mapper.row.RowMapper;
import io.github.rocketk.jorm.mapper.row.RowMapperFactory;
import io.github.rocketk.jorm.mapper.table.SnakeUpperTableModelNameMapper;
import io.github.rocketk.jorm.mapper.table.TableModelNameMapper;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.rocketk.jorm.util.JdbcUtil.setArgWithoutConversion;
import static io.github.rocketk.jorm.util.ReflectionUtil.*;


/**
 * @author pengyu
 */
public abstract class AbstractQueryInstance<T> {
    protected final TableModelNameMapper tableModelNameMapper = new SnakeUpperTableModelNameMapper();
    protected final ColumnFieldNameMapper columnFieldNameMapper = new SnakeCamelColumnFieldNameMapper();
    protected StringArrayColumnFieldMapper stringArrayColumnFieldMapper;
    protected JsonMapper jsonMapper;
    protected RowMapperFactory rowMapperFactory;
    protected RowMapper<T> rowMapper;
    protected SqlExecutor sqlExecutor;
    protected DataSource ds;
    protected Config config;
    protected Class<T> model;
    protected String table;
    protected boolean count;

    protected String rawSql;

    protected String operationId;
    protected String jormInstanceName;


    public AbstractQueryInstance(String jormInstanceName, DataSource ds, Config config, Class<T> model) {
        this(jormInstanceName, ds, config, model, null);
    }

    public AbstractQueryInstance(String jormInstanceName, DataSource ds, Config config, Class<T> model, RowMapperFactory rowMapperFactory) {
        this(jormInstanceName, ds, config, model, rowMapperFactory, null);
    }

    public AbstractQueryInstance(String jormInstanceName, DataSource ds, Config config, Class<T> model, RowMapperFactory rowMapperFactory, SqlExecutor sqlExecutor) {
        this.jormInstanceName = jormInstanceName;
        this.ds = ds;
        this.config = config;
        this.model = model;
        this.rowMapperFactory = rowMapperFactory;
        this.sqlExecutor = sqlExecutor;
    }

    protected void init() {
        initTableName();
        initRowMapper();
        initJsonMapper();
        initStringArrayColumnFieldMapper();
        initSqlExecutor();
    }

    private void initSqlExecutor() {
        if (sqlExecutor == null) {
            sqlExecutor = new DefaultSqlExecutor();
        }
    }

    private void initStringArrayColumnFieldMapper() {
        stringArrayColumnFieldMapper = new DelimiterBasedStringArrayColumnFieldMapper(config.getArrayDelimiter());
    }

    private void initJsonMapper() {
        jsonMapper = JsonMapperFactory.getJsonMapper(config.getJsonProvider());
    }

    protected void initRowMapperFactory() {
        if (rowMapperFactory == null) {
            rowMapperFactory = new DefaultRowMapperFactory(config.getArrayDelimiter(), config.getJsonProvider());
        }
    }

    protected void initTableName() {
        if (StringUtils.isNotBlank(table)) {
            return;
        }
        if (this.model == null) {
            throw new JormQueryException("either table or model is required");
        }
        final String tableNameByAnnotation = tableName(this.model);
        if (StringUtils.isNotBlank(tableNameByAnnotation)) {
            this.table = tableNameByAnnotation;
            return;
        }
        final String tableName = this.tableModelNameMapper.modelNameToTableName(model.getSimpleName());
        if (StringUtils.isBlank(tableName)) {
            throw new JormQueryException("table is null");
        }
        this.table = tableName;
    }

    protected void initRowMapper() {
        if (rowMapper == null) {
            if (model == null && !count) {
                throw new JormQueryException("either rowMapper or model is required");
            }
            initRowMapperFactory();
            rowMapper = rowMapperFactory.getRowMapper(model);
        }
    }

    protected void setArgs(PreparedStatement ps, List<Object> args) {
        setArgs(ps, args == null ? null : args.toArray());
    }

    /**
     * @param ps   PreparedStatement object
     * @param args the arguments. base type or Enum
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void setArgs(PreparedStatement ps, Object[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        try {
            for (int i = 0; i < args.length; i++) {
                final Object arg = args[i];
                final int parameterIndex = i + 1;
                if (setArgWithoutConversion(ps, parameterIndex, arg)) {
                    continue;
                }
                final Class<?> argType = arg.getClass();
                if (argType.isEnum()) {
                    final Class<Enum> enumType = (Class<Enum>) argType;
                    final JormCustomEnum customEnum = enumType.getAnnotation(JormCustomEnum.class);
                    Enum enumObj = (Enum) arg;
                    if (customEnum == null) {
                        ps.setString(parameterIndex, enumObj.name());
                    } else {
                        final Object value = getValueForCustomEnum(enumObj, customEnum.valueMethod());
                        if (!setArgWithoutConversion(ps, parameterIndex, value)) {
                            throw new IllegalArgumentException("unsupported type of argument for PreparedStatement: " + value);
                        }
                    }
                    continue;
                }
                if (argType.isArray() || List.class.isAssignableFrom(argType)) {
                    ps.setString(parameterIndex, stringArrayColumnFieldMapper.fieldToColumn(arg));
                    continue;
                }
                if (shouldUseJson(argType)) {
                    ps.setString(parameterIndex, jsonMapper.marshal(arg));
                    continue;
                }

                throw new JormQueryException("unsupported type for setting argument: " + argType.getCanonicalName());
            }
        } catch (SQLException e) {
            throw new JormQueryException(e);
        }
    }

    protected void appendWhereClause(boolean findDeletedRows, StringBuilder sql, String whereClause) {
        boolean shouldStartWithAnd = false;
//        String whereKeyword = " where ";
        if (!findDeletedRows) {
            final Optional<String> deletedAtColumnName = deletedAtColumn(model);
            if (deletedAtColumnName.isPresent()) {
                sql.append(" where ").append(deletedAtColumnName.get()).append(" is null ");
//                whereKeyword = " and ";
                shouldStartWithAnd = true;
            }
        }
        if (StringUtils.isNotBlank(whereClause)) {
            if (shouldStartWithAnd) {
                sql.append(" and (").append(whereClause).append(") ");
            } else {
                sql.append(" where ").append(whereClause).append(" ");
            }
        }
    }

}
