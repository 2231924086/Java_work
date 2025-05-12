package com.taskmanager.util;

import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static BasicDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            props.load(input);

            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(props.getProperty("db.driver"));
            dataSource.setUrl(props.getProperty("db.url"));
            dataSource.setUsername(props.getProperty("db.username"));
            dataSource.setPassword(props.getProperty("db.password"));

            // 连接池配置
            dataSource.setInitialSize(Integer.parseInt(props.getProperty("db.initialSize")));
            dataSource.setMaxTotal(Integer.parseInt(props.getProperty("db.maxTotal")));
            dataSource.setMaxIdle(Integer.parseInt(props.getProperty("db.maxIdle")));
            dataSource.setMinIdle(Integer.parseInt(props.getProperty("db.minIdle")));
            dataSource.setMaxWaitMillis(Long.parseLong(props.getProperty("db.maxWaitMillis")));

        } catch (Exception e) {
            throw new RuntimeException("初始化数据库连接池失败", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}