package org.pi.Config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;


public class DBconfig {
    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            Dotenv dotenv = Dotenv.load();

            String host = dotenv.get("DB_HOST");
            String port = dotenv.get("DB_PORT");
            String dbName = dotenv.get("DB_NAME");
            String user = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;

            HikariConfig conf = new HikariConfig();
            conf.setJdbcUrl(url);
            conf.setUsername(user);
            conf.setPassword(password);
            conf.setDriverClassName("com.mysql.cj.jdbc.Driver");

            dataSource = new HikariDataSource(conf);
        }
        return dataSource;
    }

}