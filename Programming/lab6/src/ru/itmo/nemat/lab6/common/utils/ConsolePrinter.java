package ru.itmo.nemat.lab6.common.utils;

public class ConsolePrinter implements OutputPrinter {

    public void println(Object obj) {
        System.out.println(obj);
        System.out.flush();
    }

    public void printError(Object obj) {
        System.out.println("Ошибка: " + obj);
        System.out.flush();
    }

    public void print(Object obj) { System.out.print(obj);}
}