package com.github.rocketk.jorm.mapper.row;

import com.github.rocketk.jorm.JormQueryException;
import com.github.rocketk.jorm.anno.JormCustomEnum;
import com.github.rocketk.jorm.json.JsonMapper;
import com.github.rocketk.jorm.mapper.column.ColumnFieldNameMapper;
import com.github.rocketk.jorm.mapper.column.StringArrayColumnFieldMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.github.rocketk.jorm.JdbcUtil.getFromResultSet;
import static com.github.rocketk.jorm.JdbcUtil.isSupportedTypeByJdbc;
import static com.github.rocketk.jorm.ReflectionUtil.*;

/**
 * @author pengyu
 * @date 2021/12/16
 */
public class DefaultRowMapper<T> implements RowMapper<T> {
    private final ColumnFieldNameMapper columnFieldNameMapper;
    private final StringArrayColumnFieldMapper stringArrayColumnFieldMapper;
    private final JsonMapper jsonMapper;
    private final Class<T> model;
    private final Map<String, Field> columnFieldMap = new HashMap<>();
    private final Set<String> ignoreColumns = new HashSet<>();
    private final Set<Field> ignoreFields = new HashSet<>();

    public DefaultRowMapper(ColumnFieldNameMapper columnFieldNameMapper, StringArrayColumnFieldMapper stringArrayColumnFieldMapper, JsonMapper jsonMapper, Class<T> model) {
        this.columnFieldNameMapper = columnFieldNameMapper;
        this.stringArrayColumnFieldMapper = stringArrayColumnFieldMapper;
        this.jsonMapper = jsonMapper;
        this.model = model;
    }

    @Override
    public T rowToModel(ResultSet rs, Set<String> omittedColumns) {
        initLabelFieldMap();
        try {
//            final T obj = model.newInstance();
            final T obj = model.getConstructor().newInstance();
            final ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                final String label = metaData.getColumnLabel(i);
                if (!omittedColumns.contains(label)) {
                    setField(obj, label, rs, i);
                }
            }
            return obj;
        } catch (Exception e) {
            throw new RowMapperException("cannot read row to model", e);
        }
    }

    private void initLabelFieldMap() {
        final Field[] fields = this.model.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (shouldIgnoreReadFromDb(field)) {
                this.ignoreFields.add(field);
                continue;
            }
            final String fieldName = field.getName();
            final String columnName = columnFieldNameMapper.fieldNameToColumnName(fieldName);
            this.columnFieldMap.put(columnName, field);
        }
    }

    private void setField(T obj, String column, ResultSet rs, int index) throws IllegalAccessException, SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        if (this.ignoreColumns.contains(column)) {
            return;
        }
        Field f = columnFieldMap.get(column);
        if (f == null) {
            final String guessFieldName = columnFieldNameMapper.columnNameToFieldName(column);
            try {
                f = model.getDeclaredField(guessFieldName);
                f.setAccessible(true);
                if (this.ignoreFields.contains(f)) {
                    return;
                }
                this.columnFieldMap.put(column, f);
            } catch (NoSuchFieldException e) {
                this.ignoreColumns.add(column);
                return;
            }
        }
        final Class<?> fieldType = f.getType();
        final Object columnValue = getFromResultSet(rs, index, fieldType);
        if (columnValue == null) {
            return;
        }
        if (isSupportedTypeByJdbc(fieldType)) {
            f.set(obj, columnValue);
            return;
        }
        if (Map.class.isAssignableFrom(fieldType) || shouldUseJson(fieldType)) {
            if (!(columnValue instanceof String)) {
                throw new JormQueryException("columnValue must be type of String for json field, but actual " + columnValue.getClass().getCanonicalName());
            }
            f.set(obj, this.jsonMapper.unmarshal(((String) columnValue), fieldType));
            return;
        }
        if (fieldType.isEnum()) {
            final JormCustomEnum customEnum = fieldType.getAnnotation(JormCustomEnum.class);
            final Class<Enum> enumType = (Class<Enum>) fieldType;
            if (customEnum == null) {
                final Enum enumValue = Enum.valueOf(enumType, rs.getString(index));
                f.set(obj, enumValue);
            } else {
                // columnClassName: java.lang.Integer / java.lang.String and so on.
                final Enum enumObj = parseForCustomEnum(enumType, customEnum.parseMethod(), columnValue);
                f.set(obj, enumObj);
            }
            return;
        }
        if (fieldType.isArray()) {
            if (!(columnValue instanceof String)) {
                throw new JormQueryException("columnValue must be type of String for array field, but actual " + columnValue.getClass().getCanonicalName());
            }
            f.set(obj, stringArrayColumnFieldMapper.columnToField(((String) columnValue), fieldType));
            return;
        }
        throw new JormQueryException("unsupported type: " + fieldType.getCanonicalName());
    }

}
