package io.github.rocketk.jorm;

import io.github.rocketk.BaseDataTest;
import io.github.rocketk.data.Employee;
import io.github.rocketk.jorm.conf.Config;
import io.github.rocketk.jorm.conf.ConfigFactory;
import io.github.rocketk.jorm.dialect.Dialect;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author pengyu
 *
 */
@Disabled
public class JormMysqlTest extends CrudCasesTest {

    @Override
    protected void initDataSourceInternally() throws SQLException, IOException {
        super.ds = BaseDataTest.createDataSourceForMysql();
        BaseDataTest.runScript(this.ds, "employee-mysql-schema-data.sql");
    }

    @Override
    protected Jorm createJorm() {
        final Config config = ConfigFactory.defaultConfig();
        config.setDialect(Dialect.MYSQL);
        return new Jorm(super.ds, config);
    }

    @Test
    public void testRawQuery() {
        final Jorm db = createJorm();
        final Optional<Employee> zhangsan = db.rawQuery(Employee.class, "select * from employee where name=? limit 1", "张三").first(); // non-deleted row
        final Optional<Employee> bruce = db.rawQuery(Employee.class, "select * from employee where name=? limit 1", "Bruce").first(); // deleted row
        assertTrue(zhangsan.isPresent());
        assertTrue(bruce.isPresent());
    }
}
