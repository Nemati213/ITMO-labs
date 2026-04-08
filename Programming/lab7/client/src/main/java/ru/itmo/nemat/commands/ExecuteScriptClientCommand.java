package ru.itmo.nemat.commands;


import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.utils.Runner;

import java.util.Optional;

/**
 * The type Execute script client command.
 */
public class ExecuteScriptClientCommand extends ClientCommand {

    private final Runner runner;

    /**
     * Instantiates a new Execute script client command.
     *
     * @param runner the runner
     */
    public ExecuteScriptClientCommand(Runner runner) {
        super("execute_script", "исполнить скрипт из указанного файла");
        this.runner = runner;
    }

    @Override
    public Optional<Request> apply(String[] args, String login, String password) throws InvalidCommandArgumentException {
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("Команда '" + getName() + "' ожидает ровно 1 аргумент (имя файла)!");
        }

        runner.runScript(args[0].trim());

        return Optional.empty();
    }
}