package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CollectionManager;
import ru.itmo.nemat.lab5.utility.DragonAsker;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

/**
 * Команда 'update'. Обновляет значение элемента коллекции, id которого равен заданному.
 * Требует интерактивного ввода новых характеристик объекта.
 *
 * @author Nemati213
 */
public class UpdateCommand extends Command {

    private final CollectionManager collectionManager;
    private final OutputPrinter printer;
    private final DragonAsker dragonAsker;

    /**
     * Конструктор команды.
     * @param collectionManager Менеджер коллекции для поиска элемента.
     * @param printer           Инструмент для вывода сообщений.
     * @param dragonAsker       Инструмент для интерактивного опроса новых данных дракона.
     */
    public UpdateCommand(CollectionManager collectionManager, OutputPrinter printer, DragonAsker dragonAsker) {
        super("update", "обновить значение элемента коллекции, id которого равен заданному");
        this.collectionManager = collectionManager;
        this.printer = printer;
        this.dragonAsker = dragonAsker;
    }

    /**
     * Выполняет логику команды.
     * @param args Массив аргументов команды. Ожидается один аргумент: ID дракона (long).
     * @return true, если команда выполнена успешно, иначе false.
     */
    @Override
    public boolean execute(String[] args) {
        if (args.length == 0) {
            printer.printError("Введите ID!");
            return false;
        }
        if (args.length > 1) {
            printer.printError("Слишком много аргументов!");
            return false;
        }

        try {
            long id = Long.parseLong(args[0]);

            if (!collectionManager.isExist(id)) {
                printer.println("Дракон с таким ID не найден.");
                return true;
            }
            var dragon = collectionManager.getByID(id);
            dragon.setName(dragonAsker.askName());
            dragon.setCoordinates(dragonAsker.askCoordinates());
            dragon.setAge(dragonAsker.askAge());
            dragon.setColor(dragonAsker.askColor());
            dragon.setType(dragonAsker.askDragonType());
            dragon.setCharacter(dragonAsker.askDragonCharacter());
            dragon.setCave(dragonAsker.askCave());

            printer.println("Дракон с ID " + id + " успешно обновлен!");
            return true;

        } catch (NumberFormatException e) {
            printer.printError("ID должен быть числом!");
            return false;
        }
    }
}