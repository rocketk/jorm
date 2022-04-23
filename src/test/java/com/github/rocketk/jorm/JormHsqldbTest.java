package com.github.rocketk.jorm;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.rocketk.BaseDataTest;
import com.github.rocketk.data.Employee;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * @author pengyu
 * @date 2022/4/6
 */
public class JormHsqldbTest extends CrudCasesTest {

    @Override
    protected void initDataSourceInternally() throws SQLException, IOException {
        super.ds = BaseDataTest.createDataSourceForHsqlDb();
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
        final Optional<Employee> lisi = db.rawQuery(Employee.class, "select * from employee where name=? fetch 1 row only", "李四").first(); // deleted row
        assertTrue(zhangsan.isPresent());
        assertTrue(lisi.isPresent());
    }

}
