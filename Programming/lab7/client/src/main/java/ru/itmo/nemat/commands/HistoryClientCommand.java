package ru.itmo.nemat.commands;



import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.managers.ClientCommandManager;
import ru.itmo.nemat.utils.OutputPrinter;

import java.util.Optional;


/**
 * The type History client command.
 */
public class HistoryClientCommand extends ClientCommand {

    private final OutputPrinter printer;
    private final ClientCommandManager commandManager;


    /**
     * Instantiates a new History client command.
     *
     * @param commandManager the command manager
     * @param console        the console
     */
    public HistoryClientCommand(ClientCommandManager commandManager, OutputPrinter console) {

        super("history", "показать последние 10 команд");
        this.commandManager = commandManager;
        this.printer = console;
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' не принимает аргументов!");
        }

        printer.println("Список истории команд:");
        for (String command : commandManager.getCommandHistory()) {
            printer.println(command);
        }

        return Optional.empty();
    }

}