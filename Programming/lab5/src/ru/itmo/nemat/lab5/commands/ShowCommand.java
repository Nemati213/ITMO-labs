package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CollectionManager;
import ru.itmo.nemat.lab5.models.Dragon;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

/**
 * Команда 'show'. Выводит в стандартный поток вывода все элементы коллекции в строковом представлении.
 *
 * @author Nemati213
 */
public class ShowCommand extends Command {
    private final CollectionManager collectionManager;
    private final OutputPrinter printer;

    /**
     * Конструктор команды.
     * @param collectionManager Менеджер коллекции для получения списка элементов.
     * @param printer           Инструмент для вывода сообщений.
     */
    public ShowCommand(CollectionManager collectionManager, OutputPrinter printer) {
        super("show", "вывести все элементы коллекции");
        this.collectionManager = collectionManager;
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

        if (collectionManager.collectionSize() == 0) {
            printer.println("Коллекция пуста.");
            return true;
        }

        printer.println("--- Список всех драконов в коллекции ---");

        for (Dragon dragon : collectionManager.getCollection()) {
            printer.println(dragon);
            printer.println("-----------------------");
        }

        return true;
    }
}