package com.github.rocketk.jorm;

import com.github.rocketk.BaseDataTest;
import com.github.rocketk.data.Employee;
import com.github.rocketk.jorm.conf.Config;
import com.github.rocketk.jorm.conf.ConfigFactory;
import com.github.rocketk.jorm.dialect.Dialect;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author pengyu
 * @date 2022/4/6
 */
public class JormDerbyTest extends CrudCasesTest {

    @Override
    protected void initDataSourceInternally() throws SQLException, IOException {
        super.ds = BaseDataTest.createDataSourceForDerby();
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
        final Config config = ConfigFactory.defaultConfig();
        config.setDialect(Dialect.DERBY);
        return new Jorm(ds, config);
    }

    @Test
    public void testRawQuery() {
        final Jorm db = createJorm();
        final Optional<Employee> zhangsan = db.rawQuery(Employee.class, "select * from employee where name=? fetch first 1 row only", "张三").first(); // non-deleted row
        final Optional<Employee> lisi = db.rawQuery(Employee.class, "select * from employee where name=? fetch first 1 row only", "李四").first(); // deleted row
        assertTrue(zhangsan.isPresent());
        assertTrue(lisi.isPresent());
    }
}
