package ru.itmo.nemat.utils;

import ru.itmo.nemat.managers.DialogManager;

public class UIPrinter implements OutputPrinter {

    @Override
    public void println(Object o) {
        DialogManager.showInfo(
                "ui.dialog.info.title",
                o.toString()
        );
    }

    @Override
    public void print(Object o) {
        println(o);
    }

    @Override
    public void printError(Object o) {
        DialogManager.showError(
                "ui.dialog.error.title",
                o.toString()
        );
    }
}