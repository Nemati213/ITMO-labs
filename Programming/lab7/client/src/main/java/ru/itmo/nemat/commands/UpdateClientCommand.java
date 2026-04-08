package ru.itmo.nemat.commands;

import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.DragonBuilder;
import ru.itmo.nemat.utils.DragonDTO;

import java.util.Optional;

/**
 * The type Update client command.
 */
public class UpdateClientCommand extends ClientCommand {

    private final DragonBuilder asker;

    /**
     * Instantiates a new Update client command.
     *
     * @param asker the asker
     */
    public UpdateClientCommand(DragonBuilder asker) {
        super("update", "обновить значение элемента коллекции по ID");
        this.asker = asker;
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password) throws InvalidCommandArgumentException {
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' ожидает 1 аргумент (ID)!");
        }

        try {
            Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            throw new InvalidCommandArgumentException("ID должен быть целым числом!");
        }

        DragonDTO dragon = asker.dragonDTO();
        var request = Request.builder()
                .commandName(getName())
                .args(args)
                .dragon(dragon)
                .login(login)
                .password(password)
                .build();
        return Optional.of(request);
    }
}