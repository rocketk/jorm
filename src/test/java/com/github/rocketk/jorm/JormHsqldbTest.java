package com.github.rocketk.jorm;

import com.github.rocketk.BaseDataTest;
import com.github.rocketk.data.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author pengyu
 * @date 2022/4/6
 */
//@Disabled
public class JormHsqldbTest extends CrudCasesTest {

    @Override
    protected void initDataSourceInternally() throws SQLException, IOException {
        super.ds = BaseDataTest.createDataSourceForHsqlDb();
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
        final Optional<Employee> zhangsan = db.rawQuery(Employee.class, "select * from employee where name=? fetch 1 row only", "张三").first(); // non-deleted row
        final Optional<Employee> bruce = db.rawQuery(Employee.class, "select * from employee where name=? fetch 1 row only", "Bruce").first(); // deleted row
        assertTrue(zhangsan.isPresent());
        assertTrue(bruce.isPresent());
    }

}
