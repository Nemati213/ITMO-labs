package ru.itmo.nemat.commands;



import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.DragonDTO;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class ClientCommand {

    private final String name;
    private final String description;

    public ClientCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {return name;}

    public String getDescription() {return description;}

    public boolean isProtected()   {
        return true;
    }

    public abstract Optional<Request> apply(String[] args, String login, String password, Supplier<DragonDTO> dragonSupplier) throws InvalidCommandArgumentException;
}
