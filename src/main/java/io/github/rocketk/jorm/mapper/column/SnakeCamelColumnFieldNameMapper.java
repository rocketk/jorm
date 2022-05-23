package io.github.rocketk.jorm.mapper.column;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

/**
 * @author pengyu
 * @date 2021/12/16
 */
public class SnakeCamelColumnFieldNameMapper implements ColumnFieldNameMapper {
    /**
     * examples:
     * "null" -> "null"
     * "" -> ""
     * "a" -> "a"
     * "some_function" -> "someFunction"
     * "app_i_d" -> "appID"
     * "redirect_u_r_i" -> "redirectURI"
     * "redirect_uri" -> "redirectUri"
     *
     * @param columnName
     * @return
     */
    @Override
    public String columnNameToFieldName(String columnName) {
        if (StringUtils.isBlank(columnName)) {
            return columnName;
        }
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
    }

    /**
     * examples:
     * "null" -> "null"
     * "" -> ""
     * "a" -> "a"
     * "someFunction" -> "some_function"
     * "appID" -> "app_i_d"
     * "redirectURI" -> "redirect_u_r_i"
     * "redirectUri" -> "redirect_uri"
     *
     * @param fieldName
     * @return
     */
    @Override
    public String fieldNameToColumnName(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return fieldName;
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
    }
}
