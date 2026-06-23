package ru.itmo.nemat.commands;



import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.managers.ClientCommandManager;
import ru.itmo.nemat.utils.DragonDTO;
import ru.itmo.nemat.utils.OutputPrinter;

import java.util.Optional;
import java.util.function.Supplier;


public class HistoryClientCommand extends ClientCommand {

    private final OutputPrinter printer;
    private final ClientCommandManager commandManager;


    public HistoryClientCommand(ClientCommandManager commandManager, OutputPrinter console) {

        super("history", "command.description.history");
        this.commandManager = commandManager;
        this.printer = console;
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password, Supplier<DragonDTO> dragonSupplier) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("response.error.no_args:" + getName());
        }

        printer.println("ui.history.header");
        for (String command : commandManager.getCommandHistory()) {
            printer.println(command);
        }

        return Optional.empty();
    }

}