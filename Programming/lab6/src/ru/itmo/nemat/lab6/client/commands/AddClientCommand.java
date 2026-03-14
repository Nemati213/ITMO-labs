package ru.itmo.nemat.lab6.client.commands;

import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.client.utils.DragonBuilder;
import ru.itmo.nemat.lab6.common.interaction.Request;

import java.util.Optional;


public class AddClientCommand extends ClientCommand {

    private final DragonBuilder asker;

    public AddClientCommand(DragonBuilder asker) {
        super("add", "добавить в коллекцию дракона");
        this.asker = asker;
    }

    @Override
    public Optional<Request> apply(String[] args) throws InvalidCommandArgumentException {
        if (args.length > 0)
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' не принимает аргументов!");

        var dragon = asker.dragonDTO();
        var request = new Request(getName(), args, dragon);
        return Optional.of(request);
    }
}