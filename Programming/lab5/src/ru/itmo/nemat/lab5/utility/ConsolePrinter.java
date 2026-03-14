package ru.itmo.nemat.lab5.utility;

/**
 * Реализация интерфейса OutputPrinter для вывода данных в стандартную консоль.
 *
 * @author Nemati213
 */
public class ConsolePrinter implements OutputPrinter {
    /**
     * Выводит объект в стандартный поток вывода с переносом строки.
     * @param obj Объект для вывода.
     */
    public void println(Object obj) {
        System.out.println(obj);
        System.out.flush();
    }

    /**
     * Выводит сообщение об ошибке в стандартный поток вывода.
     * @param obj Объект ошибки для вывода.
     */
    public void printError(Object obj) {
        System.out.println("Ошибка: " + obj);
        System.out.flush();
    }

    /**
     * Выводит объект в стандартный поток вывода без переноса строки.
     * @param obj Объект для вывода.
     */
    public void print(Object obj) { System.out.print(obj);}
}