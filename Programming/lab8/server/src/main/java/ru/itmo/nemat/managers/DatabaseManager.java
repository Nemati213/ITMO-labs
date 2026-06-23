package ru.itmo.nemat.managers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private final String url;
    private final String user;
    private final String password;

    private static final String URL = "jdbc.url";
    private static final String USER = "jdbc.username";
    private static final String PASSWORD = "jdbc.password";

    private static final String FILENAME = "db.properties";

    public DatabaseManager() throws IOException {
        var props = new Properties();

        try (InputStream in = new FileInputStream(FILENAME)) {
            props.load(in);
        }

        url = props.getProperty(URL);
        user = props.getProperty(USER);
        password = props.getProperty(PASSWORD);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}