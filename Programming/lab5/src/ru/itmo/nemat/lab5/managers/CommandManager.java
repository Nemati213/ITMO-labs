package ru.itmo.nemat.lab5.managers;

import ru.itmo.nemat.lab5.commands.Command;
import ru.itmo.nemat.lab5.utility.OutputPrinter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Менеджер команд. Отвечает за регистрацию, хранение и выполнение команд,
 * а также за ведение истории вызовов.
 *
 * @author Nemati213
 */
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final List<String> commandHistory = new ArrayList<>();
    private final OutputPrinter printer;

    /**
     * Конструктор менеджера команд.
     * @param console Инструмент для вывода сообщений об ошибках.
     */
    public CommandManager(OutputPrinter console) {
        this.printer = console;
    }

    /**
     * Регистрирует новую команду в системе.
     * @param command Объект команды.
     */
    public void register(Command command) {
        commands.put(command.getName(), command);
    }

    /**
     * @return Мапа всех зарегистрированных команд.
     */
    public Map<String, Command> getCommands() {
        return commands;
    }

    /**
     * @return Список последних выполненных команд.
     */
    public List<String> getCommandHistory() {
        return commandHistory;
    }

    /**
     * Добавляет выполненную команду в историю.
     * @param command Имя выполненной команды.
     */
    public void addToHistory(String command) {
        commandHistory.add(command);
        if (commandHistory.size() > 10) commandHistory.remove(0);
    }

    /**
     * Выполняет команду по её имени.
     * @param name Имя команды.
     * @param args Аргументы команды.
     * @return true, если команда выполнена успешно, иначе false.
     */
    public boolean execute(String name, String[] args) {

        Command command = commands.get(name);

        if (command == null) {
            printer.printError("Команда '" + name + "' не найдена. Введите 'help' для справки.");
            return false;
        }
        addToHistory(name);
        return command.execute(args);
    }
}