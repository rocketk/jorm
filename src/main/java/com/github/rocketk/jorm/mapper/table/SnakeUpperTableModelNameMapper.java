package com.github.rocketk.jorm.mapper.table;

import com.google.common.base.CaseFormat;

/**
 * @author pengyu
 * @date 2021/12/16
 */
public class SnakeUpperTableModelNameMapper implements TableModelNameMapper {
    @Override
    public String tableNameToModelName(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            return tableName;
        }
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName);
    }

    @Override
    public String modelNameToTableName(String modelName) {
        if (modelName == null || modelName.isEmpty()) {
            return modelName;
        }
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelName);
    }
}
