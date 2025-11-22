package org.pi.Config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;


public class DBconfig {
    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            String host = "54.208.236.160";
            String port = "3306";
            String user = "Glam1";
            String dbName = "glamsoft";
            String username = "Glam1";
            String password = "Glamsoft123*";
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