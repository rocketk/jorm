package com.github.rocketk.jorm;

import com.github.rocketk.jorm.anno.JormCustomEnum;
import com.github.rocketk.jorm.conf.Config;
import com.github.rocketk.jorm.json.JsonMapper;
import com.github.rocketk.jorm.json.JsonMapperFactory;
import com.github.rocketk.jorm.mapper.column.ColumnFieldNameMapper;
import com.github.rocketk.jorm.mapper.column.DelimiterBasedStringArrayColumnFieldMapper;
import com.github.rocketk.jorm.mapper.column.SnakeCamelColumnFieldNameMapper;
import com.github.rocketk.jorm.mapper.column.StringArrayColumnFieldMapper;
import com.github.rocketk.jorm.mapper.row.DefaultRowMapperFactory;
import com.github.rocketk.jorm.mapper.row.RowMapper;
import com.github.rocketk.jorm.mapper.row.RowMapperFactory;
import com.github.rocketk.jorm.mapper.table.SnakeUpperTableModelNameMapper;
import com.github.rocketk.jorm.mapper.table.TableModelNameMapper;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.github.rocketk.jorm.JdbcUtil.setBaseTypeArg;
import static com.github.rocketk.jorm.ReflectionUtil.*;


/**
 * @author pengyu
 * @date 2022/3/29
 */
public abstract class AbstractModelQueryInstance<T> {
    protected final TableModelNameMapper tableModelNameMapper = new SnakeUpperTableModelNameMapper();
    protected final ColumnFieldNameMapper columnFieldNameMapper = new SnakeCamelColumnFieldNameMapper();
    protected StringArrayColumnFieldMapper stringArrayColumnFieldMapper;
    protected JsonMapper jsonMapper;
    protected RowMapperFactory rowMapperFactory;
    protected RowMapper<T> rowMapper;
    protected DataSource ds;
    protected Config config;
    protected Class<T> model;
    protected String table;

    public AbstractModelQueryInstance(DataSource ds, Config config, Class<T> model) {
        this.ds = ds;
        this.config = config;
        this.model = model;
    }

    protected void init() {
        initRowMapperFactory();
        initTableName();
        initRowMapper();
        initJsonMapper();
        initStringArrayColumnFieldMapper();
    }

    private void initStringArrayColumnFieldMapper() {
        stringArrayColumnFieldMapper = new DelimiterBasedStringArrayColumnFieldMapper(config.getArrayDelimiter());
    }

    private void initJsonMapper() {
        jsonMapper = JsonMapperFactory.getJsonMapper(config.getJsonProvider());
    }

    protected void initRowMapperFactory() {
        rowMapperFactory = new DefaultRowMapperFactory(config.getArrayDelimiter(), config.getJsonProvider());
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
            if (model == null) {
                throw new JormQueryException("either rowMapper or model is required");
            }
            rowMapper = rowMapperFactory.createRowMapper(model);
        }
    }

    /**
     * @param ps
     * @param args 基本类型或枚举类型
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
                if (setBaseTypeArg(ps, parameterIndex, arg)) {
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
                        if (!setBaseTypeArg(ps, parameterIndex, value)) {
                            throw new IllegalArgumentException("unsupported type of argument for PreparedStatement: "+value);
                        }
                    }
                    continue;
                }
                if (argType.isArray()) {
                    ps.setString(parameterIndex, stringArrayColumnFieldMapper.fieldToColumn(arg));
                    continue;
                }
                if (List.class.isAssignableFrom(argType)) {
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

}
