package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.managers.CollectionManager;
import ru.itmo.nemat.lab5.models.*;
import ru.itmo.nemat.lab5.utility.DragonAsker;
import ru.itmo.nemat.lab5.utility.OutputPrinter;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 *
 * @author Nemati213
 */
public class AddCommand extends Command {

    private final CollectionManager collectionManager;
    private final OutputPrinter printer;
    private final DragonAsker asker;

    /**
     * Конструктор команды.
     * @param collectionManager Менеджер коллекции.
     * @param printer Инструмент для вывода уведомлений и ошибок в процессе работы со скриптом.
     * @param asker             Инструмент опроса пользователя.
     */
    public AddCommand(CollectionManager collectionManager, OutputPrinter printer, DragonAsker asker) {
        super("add", "добавить в коллекцию дракона");
        this.collectionManager = collectionManager;
        this.printer = printer;
        this.asker = asker;
    }

    /**
     * Выполняет команду.
     * @param args Аргументы команды (не требуются).
     * @return Успешность выполнения команды.
     */
    @Override
    public boolean execute(String... args) {
        if (args.length > 0) {
            printer.printError("Команда " + getName() + " не принимает аргументов!");
            return false;
        }

        String name = asker.askName();
        Coordinates coords = asker.askCoordinates();
        Long age = asker.askAge();
        Color color = asker.askColor();
        DragonType type = asker.askDragonType();
        DragonCharacter character = asker.askDragonCharacter();
        DragonCave cave = asker.askCave();

        collectionManager.addToCollection(name, coords, age, color, type, character, cave);

        printer.println("Дракон успешно добавлен в коллекцию!");
        return true;
    }
}