package io.github.rocketk.jorm.mapper.table;

/**
 * @author pengyu
 */
public interface TableModelNameMapper {
    String tableNameToModelName(String tableName);

    String modelNameToTableName(String modelName);
}
