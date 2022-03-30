package com.github.rocketk.jorm.mapper.column;

import com.google.common.base.CaseFormat;

/**
 * @author pengyu
 * @date 2021/12/16
 */
public class SnakeCamelColumnFieldNameMapper implements ColumnFieldNameMapper {
    @Override
    public String columnNameToFieldName(String columnName) {
        if (columnName == null || columnName.isEmpty()) {
            return columnName;
        }
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
    }

    @Override
    public String fieldNameToColumnName(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return fieldName;
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
    }
}
