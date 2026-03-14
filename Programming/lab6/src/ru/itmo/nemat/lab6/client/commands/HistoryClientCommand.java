package ru.itmo.nemat.lab6.client.commands;


import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.client.managers.ClientCommandManager;
import ru.itmo.nemat.lab6.common.utils.OutputPrinter;
import ru.itmo.nemat.lab6.common.interaction.Request;

import java.util.Optional;


public class HistoryClientCommand extends ClientCommand {

    private final OutputPrinter printer;
    private final ClientCommandManager commandManager;


    public HistoryClientCommand(ClientCommandManager commandManager, OutputPrinter console) {

        super("history", "показать последние 10 команд");
        this.commandManager = commandManager;
        this.printer = console;
    }

    @Override
    public Optional<Request> apply(String[] args) throws InvalidCommandArgumentException {
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