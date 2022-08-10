package io.github.rocketk;

import io.github.rocketk.data.Employee;
import io.github.rocketk.jorm.Jorm;
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
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import static io.github.rocketk.jorm.conf.ConfigFactory.defaultConfig;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author pengyu
 * @date 2022/8/10
 */
public class MetricsTest {
    private DataSource ds;
    private final PrometheusMeterRegistry meterRegistry = new PrometheusMeterRegistry(new PrometheusConfig() {
        @Override
        public Duration step() {
            return Duration.ofSeconds(30);
        }

        @Override
        public String get(String key) {
            return null;
        }
    });

    @BeforeEach
    public void initDataSource() throws SQLException, IOException {
        initDataSourceInternally();
    }

    @AfterEach
    public void closeDataSource() {
        if (ds != null && ds instanceof Closeable) {
            try {
                ((Closeable) ds).close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initDataSourceInternally() throws SQLException, IOException {
        ds = BaseDataTest.createDataSourceAndRunScript(DataSourceType.HIKARI, DbType.HSQLDB);
        assertNotNull(ds, "dataSource is null");
        BaseDataTest.runScript(this.ds, "employee-hsqldb-schema-data.sql");
    }

    private Jorm createJorm(SqlTagMapper sqlTagMapper) {
        final Config config = defaultConfig();
        config.setMeterRegistry(meterRegistry);
        config.setSqlTagMapper(sqlTagMapper);
        return new Jorm(ds, config);
    }

    @Test
    public void test_defaultSqlTagMapper() {
        final Jorm db = createJorm(null);
        final List<Employee> employees = db.query(Employee.class).find();
        final String scrape = meterRegistry.scrape();
//        System.out.println(scrape);
        assertTrue(scrape.contains("jorm_statement_execution_seconds_max{exception=\"None\",sql=\"Unknown\",success=\"false\",type=\"QUERY\",}"));
    }

    @Test
    public void test_rawSql() {
        final Jorm db = createJorm((rawSql, args) -> rawSql);
        final List<Employee> employees = db.query(Employee.class).find();
        final String scrape = meterRegistry.scrape();
//        System.out.println(scrape);
        assertTrue(scrape.contains("jorm_statement_execution_seconds_count{exception=\"None\",sql=\"select * from employee  where deleted_at is null\",success=\"false\",type=\"QUERY\",}"));
    }

    @Test
    public void test_removeWhereClause() {
        final Jorm db = createJorm((rawSql, args) -> StringUtils.subBeforeAny(rawSql, "where", "WHERE"));
        final List<Employee> employees = db.query(Employee.class).find();
        final String scrape = meterRegistry.scrape();
//        System.out.println(scrape);
        assertTrue(scrape.contains("jorm_statement_execution_seconds_count{exception=\"None\",sql=\"select * from employee\",success=\"false\",type=\"QUERY\",}"));
    }

}
