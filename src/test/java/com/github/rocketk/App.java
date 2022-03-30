package com.github.rocketk;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        try (final DruidDataSource ds = new DruidDataSource()) {
            ds.setInitialSize(5);
            ds.setMinIdle(2);
            ds.setMaxActive(100);
            ds.setMaxWait(15000);
            ds.setUrl("jdbc:mysql://127.0.0.1:3306/test");
            ds.setUsername("root");
            ds.setPassword("");
            ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
            ds.init();
            try (final Connection conn = ds.getConnection();
                 final PreparedStatement pstmt = conn.prepareStatement("select * from `employee` where pk=?");
            ) {
                pstmt.setObject(1, 1001);
                try (final ResultSet rs = pstmt.executeQuery()) {
                    final ResultSetMetaData metaData = rs.getMetaData();
                    final int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        final String label = metaData.getColumnLabel(i);
                        final String columnClassName = metaData.getColumnClassName(i);
                        final int displaySize = metaData.getColumnDisplaySize(i);
                        final String columnName = metaData.getColumnName(i);
                        final String typeName = metaData.getColumnTypeName(i);
                        final int type = metaData.getColumnType(i);
                        System.out.printf("label: %s, columnClassName: %s, displaySize: %d, columnName: %s, typeName: %s, type: %d\n",
                                label, columnClassName, displaySize, columnName, typeName, type);
                    }
                    System.out.println(rs.getFetchSize());
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.printf("%s\t", rs.getObject(i));
                        }
                        System.out.println();
                    }
                } catch (Exception e) {
                    throw e;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
/*
final String label = metaData.getColumnLabel(i);
final String columnClassName = metaData.getColumnClassName(i);
final int displaySize = metaData.getColumnDisplaySize(i);
final String columnName = metaData.getColumnName(i);
final String typeName = metaData.getColumnTypeName(i);
final int type = metaData.getColumnType(i);
System.out.printf("label: %s, columnClassName: %s, displaySize: %d, columnName: %s, typeName: %s, type: %d\n",
    label, columnClassName, displaySize, columnName, typeName, type);

label: pk, columnClassName: java.lang.Long, displaySize: 10, columnName: pk, typeName: INT UNSIGNED, type: 4
label: priority, columnClassName: java.lang.Integer, displaySize: 10, columnName: priority, typeName: INT, type: 4
label: title, columnClassName: java.lang.String, displaySize: 128, columnName: title, typeName: VARCHAR, type: 12
label: icon, columnClassName: java.lang.String, displaySize: 128, columnName: icon, typeName: VARCHAR, type: 12
label: app_code, columnClassName: java.lang.String, displaySize: 16, columnName: app_code, typeName: VARCHAR, type: 12
label: key_words, columnClassName: java.lang.String, displaySize: 512, columnName: key_words, typeName: VARCHAR, type: 12
label: home, columnClassName: java.lang.String, displaySize: 21845, columnName: home, typeName: TEXT, type: -1
label: tip, columnClassName: java.lang.String, displaySize: 512, columnName: tip, typeName: VARCHAR, type: 12
label: readonly, columnClassName: java.lang.Integer, displaySize: 3, columnName: readonly, typeName: TINYINT, type: -6
label: disabled, columnClassName: java.lang.Integer, displaySize: 3, columnName: disabled, typeName: TINYINT, type: -6
label: created_at, columnClassName: java.sql.Timestamp, displaySize: 19, columnName: created_at, typeName: DATETIME, type: 93
label: updated_at, columnClassName: java.sql.Timestamp, displaySize: 19, columnName: updated_at, typeName: DATETIME, type: 93
label: deleted_at, columnClassName: java.sql.Timestamp, displaySize: 19, columnName: deleted_at, typeName: DATETIME, type: 93
 */
