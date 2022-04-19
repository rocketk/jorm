package com.github.rocketk.jorm;

import com.github.rocketk.BaseDataTest;
import com.github.rocketk.jorm.conf.Config;
import com.github.rocketk.jorm.conf.ConfigFactory;
import com.github.rocketk.jorm.dialect.Dialect;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author pengyu
 * @date 2022/4/6
 */
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

}
