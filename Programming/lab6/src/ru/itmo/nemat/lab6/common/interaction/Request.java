package ru.itmo.nemat.lab6.common.interaction;

import ru.itmo.nemat.lab6.common.utils.DragonDTO;

import java.io.Serializable;

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String commandName;
    private final String[] args;
    private final DragonDTO dragon;

    public Request(String commandName, String[] args, DragonDTO dragon) {
        this.commandName = commandName;
        this.args = args;
        this.dragon = dragon;
    }

    public Request(String commandName, String[] args) {
        this(commandName, args, null);
    }

    public Request(String commandName, DragonDTO dragon) {
        this(commandName, new String[0], dragon);
    }

    public Request(String commandName) {
        this(commandName, new String[0], null);
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }

    public DragonDTO getDragon() {
        return dragon;
    }

    @Override
    public String toString() {
        return "Request{" +
                "commandName='" + commandName + '\'' +
                '}';
    }

}
