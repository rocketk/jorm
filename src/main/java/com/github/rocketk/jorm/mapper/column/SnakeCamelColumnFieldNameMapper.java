package com.github.rocketk.jorm.mapper.column;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

/**
 * @author pengyu
 * @date 2021/12/16
 */
public class SnakeCamelColumnFieldNameMapper implements ColumnFieldNameMapper {
    @Override
    public String columnNameToFieldName(String columnName) {
        if (StringUtils.isBlank(columnName)) {
            return columnName;
        }
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
    }

    @Override
    public String fieldNameToColumnName(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return fieldName;
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
    }
}
