package ru.itmo.nemat.lab6.server.managers;

import com.google.gson.*;
import java.io.*;
import java.util.logging.Logger;

public class DumpManager {
    private static final Logger logger = Logger.getLogger(DumpManager.class.getName());

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    private final String fileName;

    public DumpManager(String variableName) {
        this.fileName = System.getenv(variableName);
        if (fileName == null || fileName.isEmpty()) {
            logger.severe("Переменная окружения " + variableName + " не найдена!");
        }
    }

    public Gson getGson() {
        return gson;
    }

    public boolean writeCollection(JsonObject data) {
        if (fileName == null || fileName.isEmpty()) {
            logger.warning("Отмена сохранения: путь к файлу не задан.");
            return false;
        }

        File file = new File(fileName);
        if (file.exists() && !file.canWrite()) {
            logger.severe("Нет прав на запись в файл: " + fileName);
            return false;
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(data, writer);
            return true;
        } catch (IOException e) {
            logger.severe("Ошибка при записи в файл: " + e.getMessage());
            return false;
        }
    }

    public JsonObject readCollection() {
        if (fileName == null || fileName.isEmpty()) {
            logger.warning("Переменная окружения с путем к файлу пуста.");
            return null;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            logger.warning("Загрузочный файл не найден: " + fileName);
            return null;
        }

        if (!file.canRead()) {
            logger.severe("Нет прав на чтение файла: " + fileName);
            return null;
        }

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file))) {
            return gson.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            logger.severe("Ошибка при чтении JSON: " + e.getMessage());
            return null;
        }
    }
}