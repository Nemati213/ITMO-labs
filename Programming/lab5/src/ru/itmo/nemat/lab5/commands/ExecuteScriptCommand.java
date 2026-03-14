package ru.itmo.nemat.lab5.commands;

import ru.itmo.nemat.lab5.utility.OutputPrinter;
import ru.itmo.nemat.lab5.utility.Runner;

/**
 * Команда 'execute_script'. Обеспечивает считывание и исполнение последовательности команд
 * из внешнего текстового файла. Команды в файле должны соответствовать формату интерактивного ввода.
 *
 * @author Nemati213
 */
public class ExecuteScriptCommand extends Command {

    private final OutputPrinter printer;
    private final Runner runner;

    /**
     * Конструктор команды.
     * @param printer Инструмент для вывода уведомлений и ошибок в процессе работы со скриптом.
     * @param runner  Объект управления программой, реализующий логику переключения потоков ввода и защиты от рекурсии.
     */
    public ExecuteScriptCommand(OutputPrinter printer, Runner runner) {
        super("execute_script", "считать и исполнить скрипт из указанного файла");
        this.printer = printer;
        this.runner = runner;
    }

    /**
     * Запускает процесс выполнения скрипта.
     * @param args Массив аргументов команды.
     *             Ожидается ровно один аргумент в args[0]: путь к файлу скрипта.
     *             Аргумент не должен содержать пробелов.
     * @return true, если файл найден и успешно передан на исполнение Раннеру, иначе false.
     */
    @Override
    public boolean execute(String[] args) {
        if (args.length == 0) {
            printer.printError("Введите имя файла скрипта!");
            return false;
        }
        if (args.length > 1) {
            printer.printError("Слишком много аргументов! Путь к файлу не должен содержать пробелов.");
            return false;
        }

        String fileName = args[0].trim();

        printer.println("Попытка запуска скрипта: " + fileName);
        return runner.runScript(fileName);
    }
}