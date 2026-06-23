package ru.itmo.nemat.commands;

import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.managers.ClientCommandManager;
import ru.itmo.nemat.utils.DragonDTO;
import ru.itmo.nemat.utils.LocalizationManager;
import ru.itmo.nemat.utils.OutputPrinter;

import java.util.Optional;
import java.util.function.Supplier;

public class HelpClientCommand extends ClientCommand {

    private final ClientCommandManager commandManager;
    private final OutputPrinter printer;

    public HelpClientCommand(ClientCommandManager commandManager, OutputPrinter printer) {
        super("help", "command.description.help");
        this.commandManager = commandManager;
        this.printer = printer;
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password, Supplier<DragonDTO> dragonSupplier) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("response.error.no_args:" + getName());
        }

        StringBuilder sb = new StringBuilder();
        sb.append(LocalizationManager.getString("ui.help.header")).append("\n\n");

        for (ClientCommand command : commandManager.getCommands().values()) {
            String translatedDescription = LocalizationManager.getString(command.getDescription());
            sb.append(String.format(" %-28s | %s\n", command.getName(), translatedDescription));
        }

        printer.print(sb.toString());
        return Optional.empty();
    }


}