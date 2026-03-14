package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.utility.OutputPrinter;

/**
 * Команда 'exit'. Завершает выполнение программы без предварительного сохранения коллекции в файл.
 *
 * @author Nemati213
 */
public class ExitCommand extends Command{

    private final OutputPrinter printer;

    /**
     * Конструктор команды.
     * @param printer Инструмент для вывода сообщений.
     */
    public ExitCommand(OutputPrinter printer) {
        super("exit", "завершить программу без сохранения");
        this.printer = printer;
    }

    /**
     * Выполняет логику команды.
     * @param args Массив аргументов команды (не требуются).
     * @return true, если команда выполнена успешно, иначе false.
     */
    @Override
    public boolean execute(String[] args) {
        if (args.length > 0) {
            printer.printError("Команда '" + getName() + "' не принимает аргументов!");
            return false;
        }
        printer.println("Завершение программы...");
        System.exit(0);
        return true;
    }
}