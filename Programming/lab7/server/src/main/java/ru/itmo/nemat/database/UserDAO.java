package ru.itmo.nemat.database;

import ru.itmo.nemat.models.ResponseStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type User dao.
 */
public class UserDAO {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    /**
     * Authenticate user response status.
     *
     * @param conn     the conn
     * @param login    the login
     * @param password the password
     * @return the response status
     */
    public ResponseStatus authenticateUser(Connection conn, String login, String password) {
        String query = "SELECT id FROM app_users WHERE login = ? AND password_hash = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return ResponseStatus.AUTH_SUCCESS;
                return ResponseStatus.AUTH_ERROR;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка БД при аутентификации пользователя: " + login, e);
            return ResponseStatus.AUTH_ERROR;
        }
    }

    /**
     * Register user response status.
     *
     * @param conn     the conn
     * @param login    the login
     * @param password the password
     * @return the response status
     */
    public ResponseStatus registerUser(Connection conn, String login, String password) {
        String query = "INSERT INTO app_users (login, password_hash) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0 ? ResponseStatus.AUTH_SUCCESS : ResponseStatus.AUTH_ERROR;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) return ResponseStatus.REGISTRATION_ERROR;
            logger.log(Level.SEVERE, "Ошибка БД при регистрации пользователя: " + login, e);
            return ResponseStatus.ERROR;
        }
    }
}