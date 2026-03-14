package ru.itmo.nemat.lab6.client.commands;

import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.client.managers.ClientCommandManager;
import ru.itmo.nemat.lab6.common.utils.OutputPrinter;
import ru.itmo.nemat.lab6.common.interaction.Request;

import java.util.Optional;

public class HelpClientCommand extends ClientCommand {

    private final ClientCommandManager commandManager;
    private final OutputPrinter printer;

    public HelpClientCommand(ClientCommandManager commandManager, OutputPrinter printer) {
        super("help", "вывести справку по доступным командам");
        this.commandManager = commandManager;
        this.printer = printer;
    }

    @Override
    public Optional<Request> apply(String[] args) throws InvalidCommandArgumentException {
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