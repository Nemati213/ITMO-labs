package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CollectionManager;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Команда 'print_field_descending_age'. Выводит значения поля age всех элементов в порядке убывания.
 *
 * @author Nemati213
 */
public class PrintFieldDescendingAgeCommand extends Command {

    private final CollectionManager collectionManager;
    private final OutputPrinter printer;

    /**
     * Конструктор команды.
     * @param collectionManager Менеджер коллекции для доступа к элементам.
     * @param printer           Инструмент для вывода сообщений.
     */
    public PrintFieldDescendingAgeCommand(CollectionManager collectionManager, OutputPrinter printer) {
        super("print_field_descending_age", "вывести значения поля age всех элементов в порядке убывания");
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

        var collection = collectionManager.getCollection();
        if (collection.isEmpty()) {
            printer.println("Коллекция пуста.");
            return true;
        }

        var ages = new ArrayList<Long>();
        for (var dragon : collection) {
            ages.add(dragon.getAge());
        }
        Collections.sort(ages, Collections.reverseOrder());

        printer.println("Список возрастов драконов:");
        for (Long age : ages) {
            printer.println(age);
        }

        return true;
    }
}