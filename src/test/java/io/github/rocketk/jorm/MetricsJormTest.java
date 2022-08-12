package io.github.rocketk.jorm;

import io.github.rocketk.BaseDataTest;
import io.github.rocketk.DataSourceType;
import io.github.rocketk.DbType;
import io.github.rocketk.data.Employee;
import io.github.rocketk.jorm.conf.Config;
import io.github.rocketk.jorm.listener.SqlTagMapper;
import io.github.rocketk.jorm.util.StringUtils;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static io.github.rocketk.jorm.conf.ConfigFactory.defaultConfig;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author pengyu
 * @date 2022/8/10
 */
public class MetricsJormTest {
    private DataSource ds;
    private PrometheusMeterRegistry meterRegistry;

    @BeforeEach
    public void beforeEach() throws SQLException, IOException {
        initDataSourceInternally();
        meterRegistry = new PrometheusMeterRegistry(new PrometheusConfig() {
            @Override
            public Duration step() {
                return Duration.ofSeconds(30);
            }

            @Override
            public String get(String key) {
                return null;
            }
        });
    }

    @AfterEach
    public void afterEach() {
        if (ds != null && ds instanceof Closeable) {
            try {
                ((Closeable) ds).close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        meterRegistry.clear();
        meterRegistry.close();
    }

    private void initDataSourceInternally() throws SQLException, IOException {
        ds = BaseDataTest.createDataSourceAndRunScript(DataSourceType.HIKARI, DbType.HSQLDB);
        assertNotNull(ds, "dataSource is null");
        BaseDataTest.runScript(this.ds, "employee-hsqldb-schema-data.sql");
    }

    private Jorm createJorm(SqlTagMapper sqlTagMapper) {
        return createJorm(sqlTagMapper, null);
    }

    private Jorm createJorm(SqlTagMapper sqlTagMapper, String name) {
        final Config config = defaultConfig();
        config.setEnableEvent(true);
        config.setEnablePrintSql(true);
        config.setMeterRegistry(meterRegistry);
        config.setSqlTagMapper(sqlTagMapper);
        config.setName(name);
        return new Jorm(ds, config);
    }

    @Test
    public void test_defaultSqlTagMapper() {
        final Jorm db = createJorm(null);
        final List<Employee> employees = db.query(Employee.class).find();
        final String scrape = meterRegistry.scrape();
//        System.out.println(scrape);
        assertTrue(scrape.contains("sql=\"Unknown\""));
    }

    @Test
    public void testRawSqlMapper() {
        final Jorm db = createJorm((rawSql, args) -> rawSql);
        final List<Employee> employees = db.query(Employee.class).find();
        final String scrape = meterRegistry.scrape();
//        System.out.println(scrape);
        assertTrue(scrape.contains("sql=\"select * from employee where deleted_at is null\",success=\"true\",type=\"QUERY\""));
    }

    @Test
    public void testSubstringSqlMapper() {
        final Jorm db = createJorm((rawSql, args) -> StringUtils.subBeforeAny(rawSql, "where", "WHERE"));
        final List<Employee> employees = db.query(Employee.class).find();
        final String scrape = meterRegistry.scrape();
//        System.out.println(scrape);
        assertTrue(scrape.contains("sql=\"select * from employee\",success=\"true\",type=\"QUERY\""));
    }

    @Test
    public void testTags() {
        final Jorm db = createJorm((rawSql, args) -> rawSql, "MY-JORM-INSTANCE");
        final Optional<Employee> first = db.query(Employee.class)
                .where("pk = ?", 1)
                .operationId("findByPk")
                .first();
        final Optional<Employee> second = db.query(Employee.class)
                .where("pk = ?", 2)
                .operationId("findByPk2")
                .first();
        final List<Employee> employees = db.query(Employee.class).operationId("findAll").find();
        final long count = db.query(Employee.class).operationId("countAll").count();
        final long rows = db.mutation(Employee.class)
                .operationId("deleteAll")
                .ignoreNoWhereClauseWarning(true)
                .delete();
        final String scrape = meterRegistry.scrape();
//        System.out.println(scrape);
        assertFalse(scrape.contains("instance_name=\"Unknown\""));
        assertTrue(scrape.contains("jorm_statement_execution_seconds_count{exception=\"None\",instance_name=\"MY-JORM-INSTANCE\",operation_id=\"deleteAll\",sql=\"update employee set deleted_at=? where deleted_at is null\",success=\"true\",type=\"DELETE\",} 1.0"));
        final Jorm db2 = createJorm((rawSql, args) -> rawSql, "MY-JORM-INSTANCE2");
        final List<Employee> findAll = db2.query(Employee.class).operationId("findAll").find();
        final String scrape1 = meterRegistry.scrape();
//        System.out.println(scrape1);
        assertTrue(scrape1.contains("jorm_statement_execution_seconds_count{exception=\"None\",instance_name=\"MY-JORM-INSTANCE2\",operation_id=\"findAll\",sql=\"select * from employee where deleted_at is null\",success=\"true\",type=\"QUERY\",} 1.0"));
    }

    @Test
    public void testFail() {
        final Jorm db = createJorm((rawSql, args) -> rawSql, "MY-JORM-INSTANCE");
        final Employee obj = new Employee();
        obj.setPk(1); // already exists
        assertThrows(Exception.class, () -> {
            final boolean success = db.mutation(Employee.class)
                    .obj(obj)
                    .operationId("insertEmployee")
                    .insert();
        });
        final String scrape = meterRegistry.scrape();
//        System.out.println(scrape);
        assertTrue(scrape.contains("jorm_statement_execution_seconds_count{exception=\"None\",instance_name=\"MY-JORM-INSTANCE\",operation_id=\"insertEmployee\",sql=\"insert into employee (pk,created_at,updated_at ) values ( ?,?,? )\",success=\"false\",type=\"INSERT\",} 1.0"));
    }

    @Test
    public void testTransaction() {
        final Jorm db = createJorm((rawSql, args) -> rawSql, "MY-JORM-INSTANCE");
        final BigDecimal jackNewSalary = new BigDecimal("654321.00");
        final BigDecimal benjaminNewSalary = new BigDecimal("123456.00");
        final boolean success = db.transaction().operations(t -> {
            t.mutation(Employee.class).operationId("updateSalary").where("name=?", "Jack").set("salary", jackNewSalary).update();
            t.mutation(Employee.class).operationId("updateSalary").where("name=?", "Benjamin").set("salary", benjaminNewSalary).update();
        }).commit();
        assertTrue(success);
        db.query(Employee.class).where("name=?", "Jack").first().ifPresent(e -> assertEquals(jackNewSalary, e.getSalary()));
        db.query(Employee.class).where("name=?", "Benjamin").first().ifPresent(e -> assertEquals(benjaminNewSalary, e.getSalary()));
        final String scrape = meterRegistry.scrape();
        System.out.println(scrape);
        assertTrue(scrape.contains("jorm_statement_execution_seconds_count{exception=\"None\",instance_name=\"MY-JORM-INSTANCE\",operation_id=\"Unknown\",sql=\"select * from employee where deleted_at is null and (name=?) fetch first 1 rows only\",success=\"true\",type=\"QUERY\",} 2.0"));
        assertTrue(scrape.contains("jorm_statement_execution_seconds_count{exception=\"None\",instance_name=\"MY-JORM-INSTANCE\",operation_id=\"updateSalary\",sql=\"update employee set salary=?,updated_at=? where deleted_at is null and (name=?)\",success=\"true\",type=\"UPDATE\",} 2.0"));
    }


}
