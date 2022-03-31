package com.github.rocketk.jorm.json.jackson;

import com.github.rocketk.Employee;
import com.github.rocketk.jorm.json.JsonMapper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author pengyu
 * @date 2022/3/31
 */
public class JacksonMapperTest {

    private static JsonMapper jsonMapper;

    @BeforeClass
    public static void before() {
        jsonMapper = new JacksonMapper();
    }

    @Test
    public void marshal() {
        final Employee employee = new Employee();
        System.out.println(jsonMapper.marshal(employee));
    }
}