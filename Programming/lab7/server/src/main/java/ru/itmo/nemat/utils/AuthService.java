package ru.itmo.nemat.utils;

import ru.itmo.nemat.database.UserDAO;
import ru.itmo.nemat.models.ResponseStatus;

import java.sql.Connection;

/**
 * The type Auth service.
 */
public class AuthService {
    private final UserDAO userDAO;

    /**
     * Instantiates a new Auth service.
     *
     * @param userDAO the user dao
     */
    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticate response status.
     *
     * @param connection the connection
     * @param login      the login
     * @param password   the password
     * @return the response status
     */
    public ResponseStatus authenticate(Connection connection, String login, String password) {
        String hash = PasswordHasher.hashPassword(password);
        return userDAO.authenticateUser(connection, login, hash);
    }

    /**
     * Register response status.
     *
     * @param connection the connection
     * @param login      the login
     * @param password   the password
     * @return the response status
     */
    public ResponseStatus register(Connection connection, String login, String password) {
        String hash = PasswordHasher.hashPassword(password);
        return userDAO.registerUser(connection, login, hash);
    }
}
