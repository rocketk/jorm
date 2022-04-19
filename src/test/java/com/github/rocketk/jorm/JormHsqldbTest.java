package com.github.rocketk.jorm;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.rocketk.BaseDataTest;

import java.io.IOException;
import java.sql.SQLException;

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

}
