package io.github.rocketk.jorm.mapper.column;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author pengyu
 *
 */
public class SnakeCamelColumnFieldNameMapperTest {

    /**
     * column-field pairs
     * format: snake-camel
     */
    private String[][] testCasePairs = new String[][]{
            {null, null},
            {"", ""},
            {"a", "a"},
            {"some_function", "someFunction"},
            {"app_i_d", "appID"},
            {"redirect_u_r_i", "redirectURI"},
            {"redirect_uri", "redirectUri"},
    };

    private SnakeCamelColumnFieldNameMapper mapper;

    @BeforeEach
    public void before() {
        mapper = new SnakeCamelColumnFieldNameMapper();
    }

    @Test
    public void columnNameToFieldName() {
        Arrays.stream(testCasePairs).forEach((pair) -> {
            final String columnName = pair[0];
            final String fieldName = mapper.columnNameToFieldName(columnName);
            System.out.printf("\"%s\" -> \"%s\"%n", columnName, fieldName);
            assertEquals(pair[1], fieldName);
        });
    }

    @Test
    public void fieldNameToColumnName() {
        Arrays.stream(testCasePairs).forEach((pair) -> {
            final String fieldName = pair[1];
            final String columnName = mapper.fieldNameToColumnName(fieldName);
            System.out.printf("\"%s\" -> \"%s\"%n", fieldName, columnName);
            assertEquals(pair[0], columnName);
        });
    }
}