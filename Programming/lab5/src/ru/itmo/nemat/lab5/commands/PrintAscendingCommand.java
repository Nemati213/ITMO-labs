package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CollectionManager;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

import java.util.ArrayList;

/**
 * Команда 'print_ascending'. Выводит элементы коллекции в порядке возрастания.
 * Использует естественный порядок сортировки, определенный в классе элементов.
 *
 * @author Nemati213
 */
public class PrintAscendingCommand extends Command {

    private final CollectionManager collectionManager;
    private final OutputPrinter printer;

    /**
     * Конструктор команды.
     * @param collectionManager Менеджер коллекции для доступа к элементам.
     * @param printer           Инструмент для вывода сообщений.
     */
    public PrintAscendingCommand(CollectionManager collectionManager, OutputPrinter printer) {
        super("print_ascending", "вывести элементы коллекции в порядке возрастания");
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

        var origin = collectionManager.getCollection();

        if (origin.isEmpty()) {
            printer.println("Коллекция пуста.");
            return true;
        }

        var sortedList = new ArrayList<>(origin);
        sortedList.sort(null);

        printer.println("Элементы коллекции в порядке возрастания:");
        for (var dragon : sortedList) {
            printer.println(dragon);
            printer.println("-----------------------");
        }
        return true;
    }
}