package io.github.rocketk.jorm.mapper.table;

/**
 * @author pengyu
 * @date 2021/12/16
 */
public interface TableModelNameMapper {
    String tableNameToModelName(String tableName);

    String modelNameToTableName(String modelName);
}
