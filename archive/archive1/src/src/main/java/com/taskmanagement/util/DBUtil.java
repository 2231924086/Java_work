package com.taskmanagement.util;

import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static final BasicDataSource dataSource = new BasicDataSource();
    private static final Properties properties = new Properties();

    static {
        try {
            // 加载配置文件
            InputStream inputStream = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            properties.load(inputStream);

            // 配置数据源
            dataSource.setDriverClassName(properties.getProperty("db.driver"));
            dataSource.setUrl(properties.getProperty("db.url"));
            dataSource.setUsername(properties.getProperty("db.username"));
            dataSource.setPassword(properties.getProperty("db.password"));

            // 配置连接池
            dataSource.setInitialSize(Integer.parseInt(properties.getProperty("db.initialSize")));
            dataSource.setMaxTotal(Integer.parseInt(properties.getProperty("db.maxActive")));
            dataSource.setMaxIdle(Integer.parseInt(properties.getProperty("db.maxIdle")));
            dataSource.setMinIdle(Integer.parseInt(properties.getProperty("db.minIdle")));
            dataSource.setMaxWaitMillis(Long.parseLong(properties.getProperty("db.maxWait")));

            // 配置连接池的其他属性
            dataSource.setTestOnBorrow(true);
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setRemoveAbandonedOnBorrow(true);
            dataSource.setRemoveAbandonedTimeout(180);
            dataSource.setLogAbandoned(true);

        } catch (Exception e) {
            throw new RuntimeException("初始化数据库连接池失败", e);
        }
    }

    /**
     * 获取数据库连接
     * @return Connection 数据库连接对象
     * @throws SQLException 如果获取连接失败
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 获取数据源
     * @return DataSource 数据源对象
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 关闭数据库连接池
     */
    public static void closeDataSource() {
        try {
            dataSource.close();
        } catch (SQLException e) {
            throw new RuntimeException("关闭数据库连接池失败", e);
        }
    }

    /**
     * 测试数据库连接
     * @return boolean 连接是否成功
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
} 