package io.github.rocketk.jorm.hikari;

import io.github.rocketk.BaseDataTest;
import io.github.rocketk.DataSourceType;
import io.github.rocketk.DbType;
import io.github.rocketk.data.Employee;
import io.github.rocketk.jorm.CrudCasesTest;
import io.github.rocketk.jorm.Jorm;
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
public class JormHikariDerbyTest extends CrudCasesTest {

    @Override
    protected void initDataSourceInternally() throws SQLException, IOException {
        super.ds = BaseDataTest.createDataSourceAndRunScript(DataSourceType.HIKARI, DbType.DERBY);
        System.out.println("initDataSourceInternally: " + this.hashCode());
        Assertions.assertNotNull(super.ds, "initDataSourceInternally: dataSource is null");
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
        final Optional<Employee> zhangsan = db.rawQuery(Employee.class, "select * from employee where name=? fetch first 1 row only", "韩梅梅").first(); // non-deleted row
        final Optional<Employee> elizabeth = db.rawQuery(Employee.class, "select * from employee where name=? fetch first 1 row only", "Elizabeth").first(); // deleted row
        assertTrue(zhangsan.isPresent());
        assertTrue(elizabeth.isPresent());
    }
}
