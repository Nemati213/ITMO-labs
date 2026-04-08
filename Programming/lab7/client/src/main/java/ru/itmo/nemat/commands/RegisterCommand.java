package ru.itmo.nemat.commands;

import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.SessionAsker;

import java.util.Optional;

/**
 * The type Register command.
 */
public class RegisterCommand extends ClientCommand {

    private SessionAsker sessionAsker;

    /**
     * Instantiates a new Register command.
     *
     * @param sessionAsker the session asker
     */
    public RegisterCommand(SessionAsker sessionAsker) {
        super("register", "зарегистрироваться");
        this.sessionAsker = sessionAsker;
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password) throws InvalidCommandArgumentException {
        if (args.length > 0) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' не принимает аргументов!");
        }

        String newLogin = sessionAsker.askLogin();
        String newPassword = sessionAsker.askPasswordForRegistration();

        return Optional.of(Request.builder()
                .commandName(getName())
                .args(args)
                .login(newLogin)
                .password(newPassword)
                .build());
    }

    @Override
    public boolean isProtected() {
        return false;
    }
}
