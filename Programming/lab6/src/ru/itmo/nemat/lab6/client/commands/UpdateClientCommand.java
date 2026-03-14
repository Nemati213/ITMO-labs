package ru.itmo.nemat.lab6.client.commands;

import ru.itmo.nemat.lab6.common.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.lab6.client.utils.DragonBuilder;
import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.utils.DragonDTO;

import java.util.Optional;

public class UpdateClientCommand extends ClientCommand {

    private final DragonBuilder asker;

    public UpdateClientCommand(DragonBuilder asker) {
        super("update", "обновить значение элемента коллекции по ID");
        this.asker = asker;
    }

    @Override
    public Optional<Request> apply(String[] args) throws InvalidCommandArgumentException {
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' ожидает 1 аргумент (ID)!");
        }

        try {
            Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            throw new InvalidCommandArgumentException("ID должен быть целым числом!");
        }

        DragonDTO dragon = asker.dragonDTO();
        return Optional.of(new Request(getName(), args, dragon));
    }
}