package ru.itmo.nemat.interaction;

import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.models.ResponseStatus;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class Response implements Serializable {

    private static final long serialVersionUID = 2L;
    private final String message;
    private final ResponseStatus status;
    private final Collection<Dragon> dragons;

    public Response(String message, ResponseStatus status, Collection<Dragon> dragons) {
        this.message = message;
        this.status = status;
        this.dragons = dragons;
    }

    public boolean isSuccess()
    {
        return status == ResponseStatus.OK || status == ResponseStatus.AUTH_SUCCESS;
    }

    public Response(String message, ResponseStatus status) {
        this(message, status, Collections.emptyList());
    }

    public String getMessage() {
        return message;
    }

    public ResponseStatus getStatus() {
        return status;
    }

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
