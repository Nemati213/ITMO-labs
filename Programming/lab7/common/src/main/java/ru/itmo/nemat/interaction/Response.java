package ru.itmo.nemat.interaction;


import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.models.ResponseStatus;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * The type Response.
 */
public class Response implements Serializable {

    private static final long serialVersionUID = 2L;
    private final String message;
    private final ResponseStatus status;
    private final Collection<Dragon> dragons;

    /**
     * Instantiates a new Response.
     *
     * @param message the message
     * @param status  the status
     * @param dragons the dragons
     */
    public Response(String message, ResponseStatus status, Collection<Dragon> dragons) {
        this.message = message;
        this.status = status;
        this.dragons = dragons;
    }

    /**
     * Is success boolean.
     *
     * @return the boolean
     */
    public boolean isSuccess()
    {
        return status == ResponseStatus.OK || status == ResponseStatus.AUTH_SUCCESS;
    }

    /**
     * Instantiates a new Response.
     *
     * @param message the message
     * @param status  the status
     */
    public Response(String message, ResponseStatus status) {
        this(message, status, Collections.emptyList());
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public ResponseStatus getStatus() {
        return status;
    }

    /**
     * Gets dragons.
     *
     * @return the dragons
     */
    public Collection<Dragon> getDragons() {
        return dragons;
    }

    @Override
    public String toString() {
        return "Ответ{" +
                "сообщение='" + message + "'\\" +
        "объектов=" + (dragons == null ? 0 : dragons.size() + '}');
    }
}
