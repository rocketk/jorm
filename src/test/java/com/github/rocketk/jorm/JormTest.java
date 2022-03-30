package com.github.rocketk.jorm;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.rocketk.Employee;
import com.github.rocketk.jorm.conf.Config;
import com.github.rocketk.jorm.conf.ConfigFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
        final Employee employee = db.queryInstance(Employee.class)
//                .select("name", "salary")
                .where("name=?", "王大锤")
                .first();
        assertNotNull(employee);
        assertNotNull(employee.getPk());
        assertNotNull(employee.getName());
        assertNotNull(employee.getGender());
        assertNotNull(employee.getAcademicDegree());
        assertNotNull(employee.getSalary());
        assertNotNull(employee.getBirthDate());
        assertNotNull(employee.getTags());
        assertNotNull(employee.getAttributes());
        assertNotNull(employee.getCreatedAt());
        assertNotNull(employee.getUpdatedAt());
        assertNotNull(employee.getProfile());
        assertNull(employee.getProfileWithoutAnnotation());
        logger.info("employee: {}", employee);
    }


    @Test
    public void first_performance() {
        final long t0 = System.currentTimeMillis();
        final int n = 1000;
        for (int i = 0; i < n; i++) {
            final Employee employee = db.queryInstance(Employee.class)
                    //                .select("name", "salary")
                    .where("name=?", "王大锤")
                    .first();
        }
        final long costs = System.currentTimeMillis() - t0;
        logger.info("total costs: {} ms; per-query: {} ms.", costs, (float) costs / n);
    }


    @Test
    public void first_withSelect() {
        final Employee employee = db.queryInstance(Employee.class)
                .select("avatar")
                .where("name=?", "Jack")
                .first();
//        assertNotNull(employee.getName());
//        assertNotNull(employee.getSalary());
//        assertNull(employee.getAvatar());
        assertNull(employee.getPk());
        logger.info("employee: {}", employee);
    }

    @Test
    public void first_withOmit() {
        final Employee employee = db.queryInstance(Employee.class)
                .omit("pk", "avatar")
                .where("name=?", "Jack")
                .first();
        assertNull(employee.getAvatar());
        logger.info("employee: {}", employee);
    }


    @Test
    public void find() {
        final List<Employee> list = db.queryInstance(Employee.class)
//                .omit("avatar")
                .orderBy("name desc")
                .find();
        logger.info("list: {}", list);
    }

    @Test
    public void insert() {
        final Date now = new Date();
        final boolean success = db.updateInstance(Employee.class)
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
            final long pk = db.updateInstance(Employee.class)
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
    public void insert_withGeneratedKeys_keysArray() {
        final Date now = new Date();
        try {
            final long[] keys = db.updateInstance(Employee.class)
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