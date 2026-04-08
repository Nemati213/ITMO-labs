package ru.itmo.nemat.commands;



import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;

import java.util.Optional;

/**
 * The type Client command.
 */
public abstract class ClientCommand {

    private final String name;
    private final String description;

    /**
     * Instantiates a new Client command.
     *
     * @param name        the name
     * @param description the description
     */
    public ClientCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {return name;}

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {return description;}

    /**
     * Is protected boolean.
     *
     * @return the boolean
     */
    public boolean isProtected()   {
        return true;
    }

    /**
     * Apply optional.
     *
     * @param args     the args
     * @param login    the login
     * @param password the password
     * @return the optional
     * @throws InvalidCommandArgumentException the invalid command argument exception
     */
    public abstract Optional<Request> apply(String[] args, String login, String password) throws InvalidCommandArgumentException;
}
