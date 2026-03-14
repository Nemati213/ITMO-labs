package ru.itmo.nemat.lab5.utility;

import java.util.Scanner;

/**
 * Реализация интерфейса InputReader для чтения данных из стандартной консоли.
 *
 * @author Nemati213
 */
public class ConsoleReader implements InputReader {

    private final Scanner scanner;

    /**
     * Конструктор для создания объекта ConsoleReader.
     */
    public ConsoleReader() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Считывает следующую строку из консоли.
     * @return Считанная строка.
     */
    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Проверяет наличие следующей строки в потоке ввода.
     * @return true, если в потоке есть следующая строка, иначе false.
     */
    @Override
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Проверяет, является ли источник ввода интерактивным (консолью).
     * @return true, так как данный источник является консолью.
     */
    @Override
    public boolean isInteractive() { return true; }
}
