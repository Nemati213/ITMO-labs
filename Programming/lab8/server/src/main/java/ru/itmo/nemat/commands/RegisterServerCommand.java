package ru.itmo.nemat.commands;

import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.sql.Connection;

public class RegisterServerCommand extends ServerCommand {

    public RegisterServerCommand() {
        super("register", "регистрация нового пользователя");
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

        ResponseStatus status = userService.register(connection, login, password);

        if (status == ResponseStatus.AUTH_SUCCESS) {
            return new Response("response.register.success:" + login, ResponseStatus.AUTH_SUCCESS);
        } else if (status == ResponseStatus.REGISTRATION_ERROR) {
            return new Response("response.register.user_exists", ResponseStatus.ERROR);
        } else {
            return new Response("response.register.error", ResponseStatus.ERROR);
        }
    }

    @Override
    public boolean isProtected() {
        return false;
    }
}