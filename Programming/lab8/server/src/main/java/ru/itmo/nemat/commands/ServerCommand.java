package ru.itmo.nemat.commands;


import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ServerCommand {

    private final String name;
    private final String description;

    public ServerCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {return name;}

    public String getDescription() {return description;}

    public abstract Response execute(Request request, Connection connection, DragonService dragonService, UserService userService) throws SQLException;

    public boolean isProtected() {
        return true;
    }

}
