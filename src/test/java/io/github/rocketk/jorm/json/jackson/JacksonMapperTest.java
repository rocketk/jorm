package io.github.rocketk.jorm.json.jackson;

import io.github.rocketk.data.Employee;
import io.github.rocketk.jorm.json.JsonMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author pengyu
  */
public class JacksonMapperTest {

    private static JsonMapper jsonMapper;

    @BeforeAll
    public static void before() {
        jsonMapper = new JacksonMapper();
    }

    @Test
    public void marshal() {
        final Employee employee = new Employee();
        employee.setName("jack");
//        System.out.println(jsonMapper.marshal(employee));
        final String str = jsonMapper.marshal(employee);
        Assertions.assertNotNull(str);
        final Employee employee1 = jsonMapper.unmarshal(str, Employee.class);
        Assertions.assertNotNull(employee1);
        Assertions.assertEquals(employee.getName(), employee1.getName());
    }
}