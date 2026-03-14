package ru.itmo.nemat.lab5.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Реализация интерфейса InputReader для чтения данных из внешнего файла (скрипта).
 *
 * @author Nemati213
 */
public class FileInputReader implements InputReader {
    private final Scanner scanner;

    /**
     * Конструктор для создания объекта FileInputReader.
     * @param fileName Путь к файлу для чтения.
     * @throws FileNotFoundException Если файл по указанному пути не найден.
     */
    public FileInputReader(String fileName) throws FileNotFoundException {
        this.scanner = new Scanner(new File(fileName));
    }

    /**
     * Считывает следующую строку из файла.
     * @return Считанная строка.
     */
    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Проверяет наличие следующей строки в файле.
     * @return true, если в файле есть следующая строка, иначе false.
     */
    @Override
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Проверяет, является ли источник ввода интерактивным.
     * @return false, так как данный источник является файлом.
     */
    @Override
    public boolean isInteractive() { return false; }
}