package com.game.database;
import com.game.utils.MessageUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static HikariDataSource dataSource;

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException(MessageUtil.get("db.properties.not.found"));
            }

            Properties properties = new Properties();
            properties.load(input);

            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));
            config.setDriverClassName(properties.getProperty("db.driver"));
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.maxPoolSize")));
            config.setMinimumIdle(Integer.parseInt(properties.getProperty("db.minIdle")));
            config.setIdleTimeout(Long.parseLong(properties.getProperty("db.idleTimeout")));
            config.setMaxLifetime(Long.parseLong(properties.getProperty("db.maxLifetime")));
            config.setConnectionTimeout(Long.parseLong(properties.getProperty("db.connectionTimeout")));

            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            throw new RuntimeException(MessageUtil.get("db.properties.not.load"));
        }
    }

    public static Connection getConnection() throws SQLException {

        return dataSource.getConnection();
    }
}
