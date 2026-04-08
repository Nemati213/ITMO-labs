package ru.itmo.nemat.commands;



import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.DragonBuilder;

import java.util.Optional;


/**
 * The type Add client command.
 */
public class AddClientCommand extends ClientCommand {

    private final DragonBuilder asker;

    /**
     * Instantiates a new Add client command.
     *
     * @param asker the asker
     */
    public AddClientCommand(DragonBuilder asker) {
        super("add", "добавить в коллекцию дракона");
        this.asker = asker;
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password) throws InvalidCommandArgumentException {
        if (args.length > 0)
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' не принимает аргументов!");

        var dragon = asker.dragonDTO();
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