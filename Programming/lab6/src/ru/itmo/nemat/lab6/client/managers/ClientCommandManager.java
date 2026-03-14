package ru.itmo.nemat.lab6.client.managers;

import ru.itmo.nemat.lab6.client.commands.ClientCommand;
import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.common.utils.OutputPrinter;
import ru.itmo.nemat.lab6.common.interaction.Request;

import java.util.*;

public class ClientCommandManager {
    private final Map<String, ClientCommand> commands = new HashMap<>();
    private final List<String> commandHistory = new ArrayList<>();
    private final OutputPrinter printer;


    public ClientCommandManager(OutputPrinter console) {
        this.printer = console;
    }

    public void register(ClientCommand command) {
        commands.put(command.getName(), command);
    }

    public Map<String, ClientCommand> getCommands() {
        return commands;
    }

    public List<String> getCommandHistory() {
        return commandHistory;
    }

    public void addToHistory(String command) {
        commandHistory.add(command);
        if (commandHistory.size() > 10) commandHistory.remove(0);
    }

    public Optional<Request> apply(String name, String[] args) throws InvalidCommandArgumentException {

        ClientCommand command = commands.get(name);
        if (command == null) {
            printer.printError("Команда '" + name + "' не найдена. Введите 'help' для справки.");
            return Optional.empty();
        }
        addToHistory(name);
        return command.apply(args);
    }
}