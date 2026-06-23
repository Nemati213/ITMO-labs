package ru.itmo.nemat.managers;

import ru.itmo.nemat.commands.ClientCommand;
import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.DragonDTO;
import ru.itmo.nemat.utils.OutputPrinter;

import java.util.*;
import java.util.function.Supplier;

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

    public ClientCommand getCommand(String command) {
        return commands.get(command);
    }

    public Optional<Request> apply(String name, String[] args, String login, String password, Supplier<DragonDTO> dragonSupplier) throws InvalidCommandArgumentException {

        ClientCommand command = commands.get(name);
        if (command == null) {
            printer.printError("ui.error.command_not_found_help:" + name);
            return Optional.empty();
        }
        addToHistory(name);
        return command.apply(args, login, password, dragonSupplier);
    }
}