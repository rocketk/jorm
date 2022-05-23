package io.github.rocketk.jorm.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * @author pengyu
 */
public class StickyConnectionDataSourceWrapper implements DataSource {
    private final DataSource realDataSource;
    private TransactionalConnectionWrapper singleConnection;

    public StickyConnectionDataSourceWrapper(DataSource realDataSource) {
        this.realDataSource = realDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        synchronized (this) {
            if (singleConnection == null) {
                singleConnection = new TransactionalConnectionWrapper(realDataSource.getConnection());
            }
        }
        singleConnection.incRefers();
        return singleConnection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return realDataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return realDataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        realDataSource.setLogWriter(out);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return realDataSource.getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        realDataSource.setLoginTimeout(seconds);
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return realDataSource.getParentLogger();
    }

}
