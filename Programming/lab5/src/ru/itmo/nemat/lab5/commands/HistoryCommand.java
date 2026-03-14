package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CommandManager;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

/**
 * Команда 'history'. Выводит список последних 10 выполненных команд без их аргументов.
 *
 * @author Nemati213
 */
public class HistoryCommand extends Command {

    private final OutputPrinter printer;
    private final CommandManager commandManager;

    /**
     * Конструктор команды.
     * @param commandManager Менеджер команд для получения истории вызовов.
     * @param console        Инструмент для вывода сообщений.
     */
    public HistoryCommand(CommandManager commandManager, OutputPrinter console) {
        super("history", "показать последние 10 команд");
        this.commandManager = commandManager;
        this.printer = console;

    }

    /**
     * Выполняет логику команды.
     * @param args Массив аргументов команды (не требуются).
     * @return true, если команда выполнена успешно, иначе false.
     */
    @Override
    public boolean execute(String[] args) {
        if (args.length > 0) {
            printer.printError("Команда " + getName() + " не принимает аргументов!");
            return false;
        }
        var history = commandManager.getCommandHistory();

        if (history.isEmpty()) {
            printer.println("История команд пуста.");
            return true;
        }

        printer.println("Последние команды:");
        for (var command : history) {
            printer.println(" - " + command);
        }

        return true;
    }

}