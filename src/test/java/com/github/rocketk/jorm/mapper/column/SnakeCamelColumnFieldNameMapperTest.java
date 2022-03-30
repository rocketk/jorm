package com.github.rocketk.jorm.mapper.column;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author pengyu
 * @date 2022/3/24
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

    @Before
    public void before() {
        mapper = new SnakeCamelColumnFieldNameMapper();
    }

    @Test
    public void columnNameToFieldName() {
        Arrays.stream(testCasePairs).forEach((pair) -> assertEquals(pair[1], mapper.columnNameToFieldName(pair[0])));
    }

    @Test
    public void fieldNameToColumnName() {
        Arrays.stream(testCasePairs).forEach((pair) -> assertEquals(pair[0], mapper.fieldNameToColumnName(pair[1])));
    }
}