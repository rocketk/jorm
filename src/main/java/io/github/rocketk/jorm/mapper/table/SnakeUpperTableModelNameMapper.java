package io.github.rocketk.jorm.mapper.table;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

/**
 * @author pengyu
 */
public class SnakeUpperTableModelNameMapper implements TableModelNameMapper {
    @Override
    public String tableNameToModelName(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return tableName;
        }
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName);
    }

    @Override
    public String modelNameToTableName(String modelName) {
        if (StringUtils.isBlank(modelName)) {
            return modelName;
        }
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelName);
    }
}
