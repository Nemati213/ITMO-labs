package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CollectionManager;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

/**
 * Команда 'sort'. Сортирует коллекцию в естественном порядке.
 *
 * @author Nemati213
 */
public class SortCommand extends Command {

    private final CollectionManager collectionManager;
    private final OutputPrinter printer;

    /**
     * Конструктор команды.
     * @param collectionManager Менеджер коллекции для выполнения сортировки.
     * @param printer           Инструмент для вывода сообщений.
     */
    public SortCommand(CollectionManager collectionManager, OutputPrinter printer) {
        super("sort", "отсортировать список");
        this.collectionManager = collectionManager;
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

        collectionManager.sortCollection();
        printer.println("Коллекция успешно отсортирована.");
        return true;
    }
}