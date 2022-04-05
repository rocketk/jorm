package com.github.rocketk.jorm;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.rocketk.AcademicDegree;
import com.github.rocketk.Employee;
import com.github.rocketk.Gender;
import com.github.rocketk.jorm.conf.Config;
import com.github.rocketk.jorm.conf.ConfigFactory;
import com.google.common.collect.Lists;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author pengyu
 * @date 2022/3/24
 */
public class JormTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static DruidDataSource ds;
    private static Jorm db;

    @BeforeClass
    public static void before() throws SQLException {
        ds = new DruidDataSource();
        ds.setInitialSize(1);
        ds.setMinIdle(1);
        ds.setMaxActive(10);
        ds.setMaxWait(15000);
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf8");
        ds.setUsername("root");
        ds.setPassword("");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.init();
        final Config config = ConfigFactory.defaultConfig();
        config.setPrintSql(false);
        db = new Jorm(ds, config);
    }

    @AfterClass
    public static void after() {
        if (ds != null) {
            ds.close();
        }
    }

    @Test
    public void first() {
        final Optional<Employee> employee = db.query(Employee.class)
                .where("name=?", "王大锤")
                .first();
        assertNotNull(employee);
        assertTrue(employee.isPresent());
        final Employee e = employee.get();
        assertNotNull(e.getName());
        assertNotNull(e.getGender());
        assertNotNull(e.getAcademicDegree());
        assertNotNull(e.getSalary());
        assertNotNull(e.getBirthDate());
        assertNotNull(e.getTags());
        assertNotNull(e.getAttributes());
        assertNotNull(e.getCreatedAt());
        assertNotNull(e.getUpdatedAt());
        assertNotNull(e.getProfile());
        logger.info("e: {}", e);
    }


    @Test
    public void first_performance() {
        final long t0 = System.currentTimeMillis();
        final int n = 1000;
        for (int i = 0; i < n; i++) {
            final Optional<Employee> employee = db.query(Employee.class)
                    .where("name=?", "王大锤")
                    .first();
        }
        final long costs = System.currentTimeMillis() - t0;
        logger.info("total costs: {} ms; per-query: {} ms.", costs, (float) costs / n);
    }


    @Test
    public void first_withSelect() {
        final Optional<Employee> employee = db.query(Employee.class)
                .select("avatar")
                .where("name=?", "Jack")
                .first();
//        assertNotNull(employee.getName());
//        assertNotNull(employee.getSalary());
//        assertNull(employee.getAvatar());
        assertNotNull(employee);
        assertTrue(employee.isPresent());
        final Employee e = employee.get();
        logger.info("employee: {}", employee);
    }

    @Test
    public void first_withOmit() {
        final Optional<Employee> employee = db.query(Employee.class)
                .omit("pk", "avatar")
                .where("name=?", "Jack")
                .first();
        assertNotNull(employee);
        assertTrue(employee.isPresent());
        assertNull(employee.get().getAvatar());
        logger.info("employee: {}", employee);
    }

    @Test
    public void first_raw() {
        final Optional<Employee> employee = db.query(Employee.class).rawSql("select * from employee where name=?", "王大锤").first();
        logger.info("employee: {}", employee);
    }

    @Test
    public void first_raw_map() {
        final Optional<Map> employee = db.query(Map.class).rawSql("select * from employee where name=?", "王大锤").first();
        logger.info("employee: {}", employee);
    }

    @Test
    public void first_raw_customRowMapper() {
        final Optional<Employee> employee = db.query(Employee.class)
                .rawSql("select name, gender from employee where name=?", "王大锤")
                .rowMapper((rs, omitted)->{
                    final Employee e = new Employee();
                    e.setName(rs.getString("name"));
                    e.setGender(Gender.parse(rs.getInt("gender")));
                    return e;
                })
                .first();
        logger.info("employee: {}", employee);
    }


    @Test
    public void find() {
        final List<Employee> list = db.query(Employee.class)
//                .omit("avatar")
                .orderBy("name desc")
                .find();
        logger.info("list: {}", list);
    }

    @Test
    public void insert() {
        final Date now = new Date();
        final boolean success = db.update(Employee.class)
                .value("name", "柯达")
                .value("created_at", now)
                .value("updated_at", now)
                .execInsert();
        assertTrue(success);
    }

    @Test
    public void insert_withGeneratedKeys_firstKey() {
        final Date now = new Date();
        try {
            final long pk = db.update(Employee.class)
                    .value("name", "柯达")
                    .value("created_at", now)
                    .value("updated_at", now)
                    .execInsertAndReturnFirstKey();
            assertTrue(pk > 0);
            logger.info("generated pk: {}", pk);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void insert_withObject_withGeneratedKeys_firstKey() {
        final Employee employee = new Employee();
        final Date now = new Date();
        employee.setName("李四");
        employee.setGender(Gender.FEMALE);
        employee.setAcademicDegree(AcademicDegree.MASTER);
        employee.setTags(new String[]{"admin leader"});
        employee.setLanguages(Lists.newArrayList("java go rust"));
        employee.setUpdatedAt(now);
        employee.setCreatedAt(now);
        try {
            final long pk = db.update(Employee.class)
                    .obj(employee)
                    .omit("gender")
                    .execInsertAndReturnFirstKey();
            assertTrue(pk > 0);
            logger.info("generated pk: {}", pk);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void insert_withGeneratedKeys_keysArray() {
        final Date now = new Date();
        try {
            final long[] keys = db.update(Employee.class)
                    .value("name", "柯达")
                    .value("created_at", now)
                    .value("updated_at", now)
                    .execInsertAndReturnKeys();
            assertTrue(keys.length > 0);
            logger.info("generated keys: {}", keys);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}