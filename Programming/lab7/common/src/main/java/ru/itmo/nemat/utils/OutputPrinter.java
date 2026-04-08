package ru.itmo.nemat.utils;

/**
 * The interface Output printer.
 */
public interface OutputPrinter {

    /**
     * Println.
     *
     * @param obj the obj
     */
    void println(Object obj);

    /**
     * Print error.
     *
     * @param obj the obj
     */
    void printError(Object obj);

    /**
     * Print.
     *
     * @param obj the obj
     */
    void print(Object obj);
}