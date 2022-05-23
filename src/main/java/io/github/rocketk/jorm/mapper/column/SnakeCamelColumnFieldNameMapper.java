package io.github.rocketk.jorm.mapper.column;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

/**
 * @author pengyu
 */
public class SnakeCamelColumnFieldNameMapper implements ColumnFieldNameMapper {
    /**
     * examples:
     * <pre>{@code
     * "null" -> "null"
     * "" -> ""
     * "a" -> "a"
     * "some_function" -> "someFunction"
     * "app_i_d" -> "appID"
     * "redirect_u_r_i" -> "redirectURI"
     * "redirect_uri" -> "redirectUri"
     * }
     * </pre>
     *
     * @param columnName the name of the column
     * @return the calculated field name of the given column
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
     * <pre>{@code
     * "null" -> "null"
     * "" -> ""
     * "a" -> "a"
     * "someFunction" -> "some_function"
     * "appID" -> "app_i_d"
     * "redirectURI" -> "redirect_u_r_i"
     * "redirectUri" -> "redirect_uri"
     * }
     * </pre>
     *
     * @param fieldName the name of the field
     * @return the calculated column name of the given field
     */
    @Override
    public String fieldNameToColumnName(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return fieldName;
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
    }
}
