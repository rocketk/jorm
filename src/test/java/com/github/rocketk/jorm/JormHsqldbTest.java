package com.github.rocketk.jorm;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.github.rocketk.BaseDataTest;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.fail;

/**
 * @author pengyu
 * @date 2022/4/6
 */
public class JormHsqldbTest {
    @Test
    public void initDataBase() throws SQLException {
        try (final DruidDataSource ds = BaseDataTest.createDataSource()) {
            BaseDataTest.runScript(ds, "employee-hsqldb-schema.sql");
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
