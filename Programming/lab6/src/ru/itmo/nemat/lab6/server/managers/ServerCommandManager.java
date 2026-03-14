package ru.itmo.nemat.lab6.server.managers;

import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.server.commands.ServerCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ServerCommandManager {
    private static final Logger logger = Logger.getLogger(ServerCommandManager.class.getName());
    private static final int HISTORY_LIMIT = 50;

    private final Map<String, ServerCommand> commands = new HashMap<>();
    private final List<String> commandHistory = new ArrayList<>();

    public void register(ServerCommand command) {
        commands.put(command.getName(), command);
        logger.config("Зарегистрирована команда: " + command.getName());
    }

    public Map<String, ServerCommand> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public List<String> getCommandHistory() {
        return Collections.unmodifiableList(commandHistory);
    }

    public Response execute(Request request) {
        String commandName = request.getCommandName();
        ServerCommand command = commands.get(commandName);

        if (command == null) {
            logger.warning("Попытка выполнить неизвестную команду: " + commandName);
            return new Response("Команда '" + commandName + "' не найдена на сервере.", false);
        }

        addToHistory(commandName);
        logger.info("Выполнение команды: " + commandName);

        return command.execute(request);
    }

    private void addToHistory(String commandName) {
        commandHistory.add(commandName);
        if (commandHistory.size() > HISTORY_LIMIT) {
            commandHistory.remove(0);
        }
    }
}