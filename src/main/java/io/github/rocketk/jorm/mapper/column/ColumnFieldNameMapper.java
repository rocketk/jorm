package io.github.rocketk.jorm.mapper.column;

/**
 * @author pengyu
 */
public interface ColumnFieldNameMapper {
    String columnNameToFieldName(String columnName);

    String fieldNameToColumnName(String fieldName);
}
