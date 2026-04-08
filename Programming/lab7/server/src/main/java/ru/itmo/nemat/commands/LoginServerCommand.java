package ru.itmo.nemat.commands;

import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.utils.AuthService;

import java.sql.Connection;

/**
 * The type Login server command.
 */
public class LoginServerCommand extends ServerCommand {

    /**
     * Instantiates a new Login server command.
     */
    public LoginServerCommand() {
        super("login", "авторизация пользователя");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO,
                            CollectionManager collectionManager, AuthService authService) {

        String login = request.getLogin();
        String password = request.getPassword();

        if (login == null || login.isEmpty() || password == null || password.isEmpty())
            return new Response("Логин и пароль не могут быть пустыми", ResponseStatus.VALIDATION_ERROR);

        if (!login.matches("^[a-zA-Z0-9]+$") || !password.matches("^[a-zA-Z0-9]+$"))
            return new Response("Логин и пароль должны содержать только латинские буквы и цифры!", ResponseStatus.VALIDATION_ERROR);

        ResponseStatus status = authService.authenticate(connection, login, password);

        if (status == ResponseStatus.AUTH_SUCCESS) {
            return new Response("Авторизация успешна, добро пожаловать, " + login + " !", ResponseStatus.AUTH_SUCCESS);
        } else {
            return new Response("Неверный логин или пароль", ResponseStatus.AUTH_ERROR);
        }
    }

    @Override
    public boolean isProtected() {
        return false;
    }
}