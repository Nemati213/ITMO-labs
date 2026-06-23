package ru.itmo.nemat.services;

import ru.itmo.nemat.database.UserDAO;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.utils.PasswordHasher;

import java.sql.Connection;
import java.sql.SQLException;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public ResponseStatus login(Connection connection, String login, String password) {
        String hash = PasswordHasher.hashPassword(password);
        try{
            userDAO.findUserByCredentials(connection, login, hash);
            return ResponseStatus.AUTH_SUCCESS;
        } catch(SQLException e) {
            return ResponseStatus.AUTH_ERROR;
        }
    }

    public ResponseStatus register(Connection connection, String login, String password) {
        String hash = PasswordHasher.hashPassword(password);
        try{
            userDAO.insertUser(connection, login, hash);
            return ResponseStatus.AUTH_SUCCESS;
        } catch (SQLException e){
            if(e.getSQLState().equals("23505")) return ResponseStatus.REGISTRATION_ERROR;
            return ResponseStatus.AUTH_ERROR;
        }
    }
}
