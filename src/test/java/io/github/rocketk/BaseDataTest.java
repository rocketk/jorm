package io.github.rocketk;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author pengyu
 *
 */
public class BaseDataTest {
    public static DruidDataSource createDataSourceForHsqlDb() throws IOException, SQLException {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        ds.setUrl("jdbc:hsqldb:mem:test");
        ds.setUsername("sa");
        ds.setPassword("");
        ds.setInitialSize(1);
        ds.setMinIdle(1);
        ds.setMaxActive(1);
        ds.setMaxWait(15000);
        ds.init();
//        ds.close();
        return ds;
    }
    public static DruidDataSource createDataSourceForMysql() throws IOException, SQLException {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf8");
        ds.setUsername("root");
        ds.setPassword("");
        ds.setInitialSize(1);
        ds.setMinIdle(1);
        ds.setMaxActive(1);
        ds.setMaxWait(15000);
        ds.init();
//        ds.close();
        return ds;
    }
     public static DruidDataSource createDataSourceForDerby() throws IOException, SQLException {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        ds.setUrl("jdbc:derby:test;create=true");
        ds.setUsername("root");
        ds.setPassword("");
        ds.setInitialSize(1);
        ds.setMinIdle(1);
        ds.setMaxActive(1);
        ds.setMaxWait(15000);
        ds.init();
//        ds.close();
        return ds;
    }

    public static void runScript(DataSource ds, String resource) throws IOException, SQLException {
        try(final InputStream is = FileReader.getInputStream(resource)) {
            try (Connection connection = ds.getConnection()) {
                ScriptRunner runner = new ScriptRunner(connection, true, true);
                runner.runScript(new InputStreamReader(is));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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