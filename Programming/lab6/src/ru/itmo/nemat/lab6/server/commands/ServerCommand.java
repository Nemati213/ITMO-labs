package ru.itmo.nemat.lab6.server.commands;

import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;

import java.util.Optional;

public abstract class ServerCommand {

    private final String name;
    private final String description;

    public ServerCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {return name;}

    public String getDescription() {return description;}

    public abstract Response execute(Request request);

}
