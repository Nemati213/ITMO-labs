package ru.itmo.nemat.lab5.utility;

/**
 * Интерфейс для вывода данных.
 * Определяет методы для печати обычной информации и сообщений об ошибках.
 *
 * @author Nemati213
 */
public interface OutputPrinter {
    /**
     * Выводит объект с переносом строки.
     * @param obj Объект для вывода.
     */
    void println(Object obj);

    /**
     * Выводит сообщение об ошибке.
     * @param obj Объект ошибки для вывода.
     */
    void printError(Object obj);

    /**
     * Выводит объект без переноса строки.
     * @param obj Объект для вывода.
     */
    void print(Object obj);
}