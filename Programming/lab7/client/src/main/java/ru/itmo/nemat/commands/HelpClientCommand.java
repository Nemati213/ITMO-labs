package ru.itmo.nemat.commands;

import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.managers.ClientCommandManager;
import ru.itmo.nemat.utils.OutputPrinter;

import java.util.Optional;

/**
 * The type Help client command.
 */
public class HelpClientCommand extends ClientCommand {

    private final ClientCommandManager commandManager;
    private final OutputPrinter printer;

    /**
     * Instantiates a new Help client command.
     *
     * @param commandManager the command manager
     * @param printer        the printer
     */
    public HelpClientCommand(ClientCommandManager commandManager, OutputPrinter printer) {
        super("help", "вывести справку по доступным командам");
        this.commandManager = commandManager;
        this.printer = printer;
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' не принимает аргументов!");
        }

        printer.println("Список доступных команд:");
        for (ClientCommand command : commandManager.getCommands().values()) {
            printer.println(String.format(" %-25s : %s", command.getName(), command.getDescription()));
        }

        return Optional.empty();
    }

}