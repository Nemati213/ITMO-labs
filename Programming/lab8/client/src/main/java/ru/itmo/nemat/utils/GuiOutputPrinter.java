package ru.itmo.nemat.utils;

import ru.itmo.nemat.managers.DialogManager;

public class GuiOutputPrinter implements OutputPrinter {

    private final StringBuilder buffer;
    private static GuiOutputPrinter instance;

    private GuiOutputPrinter() {
        buffer = new StringBuilder();
    }
    public static GuiOutputPrinter getInstance() {
        if(instance !=  null )
            return instance;
        instance = new GuiOutputPrinter();
        return instance;
    }

    @Override
    public void println(Object o) {
        buffer.append(LocalizationManager.format(o.toString()));
        buffer.append('\n');
    }

    @Override
    public void print(Object o) {
        buffer.append(LocalizationManager.format(o.toString()));
    }

    @Override
    public void printError(Object o) {
        DialogManager.showError(
                LocalizationManager.getString("ui.dialog.error.title"),
                LocalizationManager.format(o.toString())
        );
    }

    public void getAndClear(String commandName) {
        String result = buffer.toString().trim();
        if (!result.isEmpty()) {
            DialogManager.showInfo(commandName, result);
            buffer.setLength(0);
        }
    }
}