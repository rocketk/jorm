package com.github.rocketk;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author pengyu
 * @date 2022/4/4
 */
public class BaseDataTest {
//    public static final String EMPLOYEE_PROPERTIES = "com/github/rocketk/databases/employee/employee-hsqldb.properties";
    public static final String EMPLOYEE_PROPERTIES = "com/github/rocketk/databases/employee/employee-hsqldb.properties";
    public static final String EMPLOYEE_DDL = "com/github/rocketk/databases/employee/employee-hsqldb-schema.sql";
    public static final String EMPLOYEE_DATA = "com/github/rocketk/databases/employee/employee-hsqldb-dataload.sql";

//    public static DataSource createDataSource(String resource) throws IOException {
//        final ResourceBundle bundle = ResourceBundle.getBundle(EMPLOYEE_PROPERTIES);
//
//        PooledDataSource ds = new PooledDataSource();
//        ds.setDriver(props.getProperty("driver"));
//        ds.setUrl(props.getProperty("url"));
//        ds.setUsername(props.getProperty("username"));
//        ds.setPassword(props.getProperty("password"));
//        return ds;
//    }
//
//    public static void runScript(DataSource ds, String resource) throws IOException, SQLException {
//        try (Connection connection = ds.getConnection()) {
//            ScriptRunner runner = new ScriptRunner(connection);
//            runner.setAutoCommit(true);
//            runner.setStopOnError(false);
//            runner.setLogWriter(null);
//            runner.setErrorLogWriter(null);
//            runScript(runner, resource);
//        }
//    }
//
//    public static void runScript(ScriptRunner runner, String resource) throws IOException, SQLException {
//        try (Reader reader = Resources.getResourceAsReader(resource)) {
//            runner.runScript(reader);
//        }
//    }
//
//    public static DataSource createBlogDataSource() throws IOException, SQLException {
//        DataSource ds = createUnpooledDataSource(BLOG_PROPERTIES);
//        runScript(ds, BLOG_DDL);
//        runScript(ds, BLOG_DATA);
//        return ds;
//    }
//
//    public static DataSource createJPetstoreDataSource() throws IOException, SQLException {
//        DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
//        runScript(ds, JPETSTORE_DDL);
//        runScript(ds, JPETSTORE_DATA);
//        return ds;
//    }
}
