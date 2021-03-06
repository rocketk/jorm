package io.github.rocketk.jorm.druid;

import io.github.rocketk.BaseDataTest;
import io.github.rocketk.DataSourceType;
import io.github.rocketk.DbType;
import io.github.rocketk.data.Employee;
import io.github.rocketk.jorm.CrudCasesTest;
import io.github.rocketk.jorm.Jorm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author pengyu
 *
 */
//@Disabled
public class JormDruidHsqldbTest extends CrudCasesTest {

    @Override
    protected void initDataSourceInternally() throws SQLException, IOException {
        super.ds = BaseDataTest.createDataSourceAndRunScript(DataSourceType.DRUID, DbType.HSQLDB);
        Assertions.assertNotNull(super.ds, "dataSource is null");
        BaseDataTest.runScript(this.ds, "employee-hsqldb-schema-data.sql");
    }

    @Override
    protected Jorm createJorm() {
        return new Jorm(super.ds);
    }


    @Test
    public void testRawQuery() {
        final Jorm db = createJorm();
        final Optional<Employee> zhangsan = db.rawQuery(Employee.class, "select * from employee where name=? fetch 1 row only", "韩梅梅").first(); // non-deleted row
        final Optional<Employee> elizabeth = db.rawQuery(Employee.class, "select * from employee where name=? fetch 1 row only", "Elizabeth").first(); // deleted row
        assertTrue(zhangsan.isPresent());
        assertTrue(elizabeth.isPresent());
    }

}
