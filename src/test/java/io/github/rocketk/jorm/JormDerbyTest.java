package io.github.rocketk.jorm;

import io.github.rocketk.BaseDataTest;
import io.github.rocketk.data.Employee;
import io.github.rocketk.jorm.conf.Config;
import io.github.rocketk.jorm.conf.ConfigFactory;
import io.github.rocketk.jorm.dialect.Dialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * @author pengyu
 *
 */
//@Disabled
public class JormDerbyTest extends CrudCasesTest {

    @Override
    protected void initDataSourceInternally() throws SQLException, IOException {
        super.ds = BaseDataTest.createDataSourceForDerby();
        System.out.println("initDataSourceInternally: " + this.hashCode());
        Assertions.assertNotNull(super.ds, "initDataSourceInternally: dataSource is null");
        try {
            BaseDataTest.runScript(this.ds, "employee-derby-drop-table.sql");
        } catch (SQLException e) {
            if (e.getMessage().contains("because it does not exist")) {
                //
            } else {
                fail(e.getMessage());
            }
        }
        BaseDataTest.runScript(this.ds, "employee-derby-schema-data.sql");
    }

    @Override
    protected Jorm createJorm() {
        Assertions.assertNotNull(super.ds, "createJorm: dataSource is null");
        final Config config = ConfigFactory.defaultConfig();
        config.setDialect(Dialect.DERBY);
        return new Jorm(ds, config);
    }

    @Test
    public void testRawQuery() {
        final Jorm db = createJorm();
        final Optional<Employee> zhangsan = db.rawQuery(Employee.class, "select * from employee where name=? fetch first 1 row only", "张三").first(); // non-deleted row
        final Optional<Employee> bruce = db.rawQuery(Employee.class, "select * from employee where name=? fetch first 1 row only", "Bruce").first(); // deleted row
        assertTrue(zhangsan.isPresent());
        assertTrue(bruce.isPresent());
    }
}
