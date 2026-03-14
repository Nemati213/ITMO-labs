package ru.itmo.nemat.lab5.managers;

import ru.itmo.nemat.lab5.models.Dragon;

import java.io.*;

import java.lang.reflect.Type;
import java.util.Stack;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ru.itmo.nemat.lab5.utility.OutputPrinter;


/**
 * Менеджер дампов. Отвечает за сохранение коллекции в файл и её загрузку из файла в формате JSON.
 *
 * @author Nemati213
 */
public class DumpManager {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    private final String fileName;
    private final OutputPrinter printer;

    /**
     * Конструктор менеджера дампов.
     * @param variableName Имя переменной окружения, содержащей путь к файлу.
     * @param printer      Инструмент для вывода сообщений.
     */
    public DumpManager(String variableName, OutputPrinter printer) {
        this.fileName = System.getenv(variableName);
        this.printer = printer;

    }

    public Gson getGson() {
        return gson;
    }

    /**
     * Записывает коллекцию в файл.
     * @param data Коллекция драконов для сохранения и LastID
     */
    public void writeCollection(JsonObject data) {
        if (fileName == null || fileName.isEmpty()) {
            printer.printError("Переменная окружения не найдена!");
            return;
        }

        File file = new File(fileName);
        if (file.exists() && !file.canWrite()) {
            printer.printError("Нет прав на запись в файл!");
            return;
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            printer.printError("Не удалось записать данные в файл!");
        }
    }


    /**
     * Считывает коллекцию из файла.
     * @return Считанная коллекция драконов. В случае ошибки возвращает пустой стек.
     */
    public JsonObject readCollection() {
        if (fileName != null && !fileName.isEmpty()) {
            File file = new File(fileName);
            if (file.exists() && !file.canRead()) {
                printer.printError("Нет прав на чтение файла!");
                return null;
            }

            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName))) {

                return gson.fromJson(reader, JsonObject.class);

            } catch (FileNotFoundException e) {
                printer.printError("Загрузочный файл не найден!");
            } catch (Exception e) {
                printer.printError("Ошибка при загрузке данных: " + e.getMessage());
            }
        } else {
            printer.printError("Переменная окружения с путем к файлу не найдена!");
        }
        return null;
    }
}