package ru.itmo.nemat.commands;

import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.sql.Connection;

public class LoginServerCommand extends ServerCommand {

    public LoginServerCommand() {
        super("login", "авторизация пользователя");
    }

    @Override
    public Response execute(Request request, Connection connection, DragonService dragonService, UserService userService) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            return new Response("response.auth.empty_fields", ResponseStatus.VALIDATION_ERROR);
        }

        if (!login.matches("^[a-zA-Z0-9]+$") || !password.matches("^[a-zA-Z0-9]+$")) {
            return new Response("response.auth.invalid_format", ResponseStatus.VALIDATION_ERROR);
        }

        ResponseStatus status = userService.login(connection, login, password);

        if (status == ResponseStatus.AUTH_SUCCESS) {
            return new Response("response.auth.success:" + login, ResponseStatus.AUTH_SUCCESS);
        } else {
            return new Response("response.auth.error", ResponseStatus.AUTH_ERROR);
        }
    }

    @Override
    public boolean isProtected() {
        return false;
    }
}