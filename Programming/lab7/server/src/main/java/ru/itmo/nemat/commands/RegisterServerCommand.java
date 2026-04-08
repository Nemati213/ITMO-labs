package ru.itmo.nemat.commands;

import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.utils.AuthService;

import java.sql.Connection;

/**
 * The type Register server command.
 */
public class RegisterServerCommand extends ServerCommand {

    /**
     * Instantiates a new Register server command.
     */
    public RegisterServerCommand() {
        super("register", "регистрация нового пользователя");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonDAO dragonDAO,
                            CollectionManager collectionManager, AuthService authService) {

        String login = request.getLogin();
        String password = request.getPassword();

        if (login == null || login.isEmpty() || password == null || password.isEmpty())
            return new Response("Логин и пароль не могут быть пустыми", ResponseStatus.VALIDATION_ERROR);

        if (!login.matches("^[a-zA-Z0-9]+$") || !password.matches("^[a-zA-Z0-9]+$"))
            return new Response("Логин и пароль должны содержать только латинские буквы и цифры", ResponseStatus.VALIDATION_ERROR);

        ResponseStatus status = authService.register(connection, login, password);

        if (status == ResponseStatus.AUTH_SUCCESS) {
            return new Response("Пользователь " + login + " успешно зарегистрирован", ResponseStatus.AUTH_SUCCESS);
        } else if (status == ResponseStatus.REGISTRATION_ERROR) {
            return new Response("Пользователь с таким логином уже существует", ResponseStatus.ERROR);
        } else {
            return new Response("Ошибка при регистрации", ResponseStatus.ERROR);
        }
    }

    @Override
    public boolean isProtected() {
        return false;
    }
}
