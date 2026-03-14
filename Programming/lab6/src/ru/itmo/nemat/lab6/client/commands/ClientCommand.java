package ru.itmo.nemat.lab6.client.commands;

import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.common.interaction.Request;

import java.util.Optional;

public abstract class ClientCommand {

    private final String name;
    private final String description;

    public ClientCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {return name;}

    public String getDescription() {return description;}

    public abstract Optional<Request> apply(String[] args) throws InvalidCommandArgumentException;

}
