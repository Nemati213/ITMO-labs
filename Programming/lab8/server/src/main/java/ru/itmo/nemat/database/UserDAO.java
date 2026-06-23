package ru.itmo.nemat.database;

import ru.itmo.nemat.models.ResponseStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    private static final String LOGIN_QUERY = "SELECT id FROM app_users WHERE login = ? AND password_hash = ?";
    private static final String REGISTER_QUERY = "INSERT INTO app_users (login, password_hash) VALUES (?, ?)";
    private static final String SELECT_ID_BY_LOGIN = "SELECT id FROM app_users WHERE login = ?";

    public long findUserByCredentials(Connection conn, String login, String password) throws SQLException {

        try (PreparedStatement pstmt = conn.prepareStatement(LOGIN_QUERY)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
                throw new SQLException("error.database.auth");
            }
        }
    }


    public void insertUser(Connection conn, String login, String passwordHash) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(REGISTER_QUERY)) {
            pstmt.setString(1, login);
            pstmt.setString(2, passwordHash);
            pstmt.executeUpdate();
        }
    }

    public long getUserIdByLogin(Connection connection, String login) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ID_BY_LOGIN)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getLong(1);
                else
                    throw new SQLException("error.database.user_not_found:" + login);
            }
        }
    }
}
