package com.github.rocketk.jorm.mapper.table;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author pengyu
 * @date 2022/3/24
 */
public class SnakeUpperTableModelNameMapperTest {

    /**
     * table-model pairs
     * format: snake-uppercamel
     */
    private String[][] tableModelPairs = new String[][]{
            {null, null},
            {"", ""},
            {"a", "A"},
            {"some_table", "SomeTable"},
            {"app_i_d", "AppID"},
            {"employee", "Employee"},
            {"client_redirect_uri", "ClientRedirectUri"},
    };

    private SnakeUpperTableModelNameMapper mapper;

    @BeforeEach
    public void before() {
        mapper = new SnakeUpperTableModelNameMapper();
    }


    @Test
    public void tableNameToModelName() {
        Arrays.stream(tableModelPairs).forEach((pair) -> assertEquals(pair[1], mapper.tableNameToModelName(pair[0])));
    }

    @Test
    public void modelNameToTableName() {
        Arrays.stream(tableModelPairs).forEach((pair) -> assertEquals(pair[0], mapper.modelNameToTableName(pair[1])));
    }
}