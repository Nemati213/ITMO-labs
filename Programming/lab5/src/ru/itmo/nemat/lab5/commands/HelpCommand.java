package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CommandManager;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

import java.util.Collection;

/**
 * Команда 'help'. Выводит справку по всем доступным командам приложения.
 *
 * @author Nemati213
 */
public class HelpCommand extends Command {
    private final CommandManager commandManager;
    private final OutputPrinter printer;

    /**
     * Конструктор команды.
     * @param commandManager Менеджер команд для получения списка доступных команд.
     * @param printer        Инструмент для вывода сообщений.
     */
    public HelpCommand(CommandManager commandManager, OutputPrinter printer) {
        super("help", "вывести справку по доступным командам");
        this.commandManager = commandManager;
        this.printer = printer;
    }

    /**
     * Выполняет логику команды.
     * @param args Массив аргументов команды (не требуются).
     * @return true, если команда выполнена успешно, иначе false.
     */
    @Override
    public boolean execute(String... args) {
        if (args.length > 0) {
            printer.printError("Команда '" + getName() + "' не принимает аргументов!");
            return false;
        }
        Collection<Command> commands = commandManager.getCommands().values();
        printer.println("--- Список доступных команд ---");

        for (Command command : commands) {
            printer.println(String.format("%-30s | %s", command.getName(), command.getDescription()));
        }

        return true;
    }
}