package ru.itmo.nemat.lab6.common.interaction;

import ru.itmo.nemat.lab6.common.models.Dragon;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class Response implements Serializable {

    private static final long serialVersionUID = 2L;
    private final String message;
    private final boolean success;
    private final Collection<Dragon> dragons;

    public Response(String message, boolean success, Collection<Dragon> dragons) {
        this.message = message;
        this.success = success;
        this.dragons = dragons;
    }

    public Response(String message, boolean success) {
        this(message, success, Collections.emptyList());
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
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
