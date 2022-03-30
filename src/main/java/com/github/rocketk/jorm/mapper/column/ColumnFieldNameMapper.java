package com.github.rocketk.jorm.mapper.column;

/**
 * @author pengyu
 * @date 2021/12/16
 */
public interface ColumnFieldNameMapper {
    String columnNameToFieldName(String columnName);

    String fieldNameToColumnName(String fieldName);
}
