package ru.itmo.nemat.managers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The type Database manager.
 */
public class DatabaseManager {
    private final String url;
    private final String user;
    private final String password;

    /**
     * Instantiates a new Database manager.
     *
     * @throws IOException the io exception
     */
    public DatabaseManager() throws IOException {
        var props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("db.properties"))) {
            props.load(in);
        }
        url = props.getProperty("jdbc.url");
        user = props.getProperty("jdbc.user");
        password = props.getProperty("jdbc.password");
    }

    /**
     * Gets connection.
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}
