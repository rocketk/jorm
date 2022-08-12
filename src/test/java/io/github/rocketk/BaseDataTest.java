package io.github.rocketk;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author pengyu
 */
public class BaseDataTest {
    private static HikariDataSource createHikariDataSource(String driver, String url, String username, String password) {
        final HikariConfig c = new HikariConfig();
        c.setDriverClassName(driver);
        c.setJdbcUrl(url);
        c.setUsername(username);
        c.setPassword(password);
        c.addDataSourceProperty("cachePrepStmts", "true");
        c.addDataSourceProperty("prepStmtCacheSize", "250");
        c.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        c.addDataSourceProperty("allowMultiQueries", "true");
        return new HikariDataSource(c);
    }

    private static DruidDataSource createDruidDataSource(String driver, String url, String username, String password) {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setInitialSize(1);
        ds.setMinIdle(1);
        ds.setMaxActive(1);
        ds.setMaxWait(15000);
        try {
            ds.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        ds.close();
        return ds;
    }

    public static DataSource createDataSource(DataSourceType dataSourceType, DbType dbType) {
        final String driver;
        final String url;
        final String user;
        final String password;
        switch (dbType) {
            case MYSQL:
                driver = "com.mysql.cj.jdbc.Driver";
                url = "jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf8";
                user = "root";
                password = "";
                break;
            case DERBY:
                driver = "org.apache.derby.jdbc.EmbeddedDriver";
                url = "jdbc:derby:test;create=true";
                user = "root";
                password = "";
                break;
            case HSQLDB:
            default:
                driver = "org.hsqldb.jdbc.JDBCDriver";
                url = "jdbc:hsqldb:mem:test";
                user = "sa";
                password = "";
                break;
        }
        switch (dataSourceType) {
            case DRUID:
                return createDruidDataSource(driver, url, user, password);
            case HIKARI:
            default:
                return createHikariDataSource(driver, url, user, password);
        }
    }

//    public static DruidDataSource createDataSourceForHsqlDb() throws IOException, SQLException {
//        DruidDataSource ds = new DruidDataSource();
//        ds.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
//        ds.setUrl("jdbc:hsqldb:mem:test");
//        ds.setUsername("sa");
//        ds.setPassword("");
//        ds.setInitialSize(1);
//        ds.setMinIdle(1);
//        ds.setMaxActive(1);
//        ds.setMaxWait(15000);
//        ds.init();
////        ds.close();
//        return ds;
//    }
//
//    public static DruidDataSource createDataSourceForMysql() throws IOException, SQLException {
//        DruidDataSource ds = new DruidDataSource();
//        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        ds.setUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf8");
//        ds.setUsername("root");
//        ds.setPassword("");
//        ds.setInitialSize(1);
//        ds.setMinIdle(1);
//        ds.setMaxActive(1);
//        ds.setMaxWait(15000);
//        ds.init();
////        ds.close();
//        return ds;
//    }
//
//    public static DruidDataSource createDataSourceForDerby() throws IOException, SQLException {
//        DruidDataSource ds = new DruidDataSource();
//        ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
//        ds.setUrl("jdbc:derby:test;create=true");
//        ds.setUsername("root");
//        ds.setPassword("");
//        ds.setInitialSize(1);
//        ds.setMinIdle(1);
//        ds.setMaxActive(1);
//        ds.setMaxWait(15000);
//        ds.init();
////        ds.close();
//        return ds;
//    }

//    public static void runScript(DataSource ds, String resource) throws IOException, SQLException {
//        try (final InputStream is = FileReader.getInputStream(resource)) {
//            try (Connection connection = ds.getConnection()) {
//                ScriptRunner runner = new ScriptRunner(connection);
//                runner.runScript(new InputStreamReader(is));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void runScript(DataSource ds, String resource) throws IOException, SQLException {
        try (Connection connection = ds.getConnection()) {
            ScriptRunner runner = new ScriptRunner(connection);
            runner.setAutoCommit(true);
            runner.setStopOnError(false);
//            runner.setLogWriter(new PrintWriter(System.out));
            runner.setLogWriter(null);
            runner.setErrorLogWriter(new PrintWriter(System.err));
            runScript(runner, resource);
        }
    }

    public static void runScript(ScriptRunner runner, String resource) throws IOException, SQLException {
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            runner.runScript(reader);
        }
    }

    public static DataSource createDataSourceAndRunScript(DataSourceType dataSourceType, DbType dbType) throws SQLException, IOException {
        final DataSource ds = createDataSource(dataSourceType, dbType);
        switch (dbType) {
            case MYSQL:
                runScript(ds, "employee-mysql-schema-data.sql");
                break;
            case DERBY:
                try {
                    runScript(ds, "employee-derby-drop-table.sql");
                } catch (SQLException e) {
                    if (!e.getMessage().contains("because it does not exist")) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
                runScript(ds, "employee-derby-schema-data.sql");
                break;
            case HSQLDB:
            default:
                runScript(ds, "employee-hsqldb-schema-data.sql");
                break;
        }
        return ds;
    }
//
//    public static void runScript(io.github.rocketk.ScriptRunner runner, String resource) throws IOException, SQLException {
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
