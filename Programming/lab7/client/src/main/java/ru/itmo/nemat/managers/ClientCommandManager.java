package ru.itmo.nemat.managers;

import ru.itmo.nemat.commands.ClientCommand;
import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.OutputPrinter;

import java.util.*;

/**
 * The type Client command manager.
 */
public class ClientCommandManager {
    private final Map<String, ClientCommand> commands = new HashMap<>();
    private final List<String> commandHistory = new ArrayList<>();
    private final OutputPrinter printer;


    /**
     * Instantiates a new Client command manager.
     *
     * @param console the console
     */
    public ClientCommandManager(OutputPrinter console) {
        this.printer = console;
    }

    /**
     * Register.
     *
     * @param command the command
     */
    public void register(ClientCommand command) {
        commands.put(command.getName(), command);
    }

    /**
     * Gets commands.
     *
     * @return the commands
     */
    public Map<String, ClientCommand> getCommands() {
        return commands;
    }

    /**
     * Gets command history.
     *
     * @return the command history
     */
    public List<String> getCommandHistory() {
        return commandHistory;
    }

    /**
     * Add to history.
     *
     * @param command the command
     */
    public void addToHistory(String command) {
        commandHistory.add(command);
        if (commandHistory.size() > 10) commandHistory.remove(0);
    }

    /**
     * Apply optional.
     *
     * @param name     the name
     * @param args     the args
     * @param login    the login
     * @param password the password
     * @return the optional
     * @throws InvalidCommandArgumentException the invalid command argument exception
     */
    public Optional<Request> apply(String name, String[] args, String login, String password) throws InvalidCommandArgumentException {

        ClientCommand command = commands.get(name);
        if (command == null) {
            printer.printError("Команда '" + name + "' не найдена. Введите 'help' для справки.");
            return Optional.empty();
        }
        addToHistory(name);
        return command.apply(args, login, password);
    }
}