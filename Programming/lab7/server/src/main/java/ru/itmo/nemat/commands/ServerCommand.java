package ru.itmo.nemat.commands;


import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.utils.AuthService;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The type Server command.
 */
public abstract class ServerCommand {

    private final String name;
    private final String description;

    /**
     * Instantiates a new Server command.
     *
     * @param name        the name
     * @param description the description
     */
    public ServerCommand(String name, String description) {
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
     * Execute response.
     *
     * @param request           the request
     * @param connection        the connection
     * @param dragonDAO         the dragon dao
     * @param collectionManager the collection manager
     * @param authService       the auth service
     * @return the response
     * @throws SQLException the sql exception
     */
    public abstract Response execute(Request request, Connection connection, DragonDAO dragonDAO, CollectionManager collectionManager, AuthService authService) throws SQLException;

    /**
     * Is protected boolean.
     *
     * @return the boolean
     */
    public boolean isProtected() {
        return true;
    }

}
