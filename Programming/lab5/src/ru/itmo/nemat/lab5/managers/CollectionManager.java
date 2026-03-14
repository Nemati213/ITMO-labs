package ru.itmo.nemat.lab5.managers;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import ru.itmo.nemat.lab5.models.*;

import java.lang.reflect.Type;

import java.util.Date;
import java.util.Stack;

/**
 * Менеджер коллекции. Отвечает за хранение, обработку и выполнение операций над коллекцией драконов.
 *
 * @author Nemati213
 */
public class CollectionManager {
    private final DumpManager dumpManager;
    private Stack<Dragon> collection = new Stack<>();
    private Date lastInitTime;
    private Date lastSaveTime;

    private Long lastID;

    /**
     * Конструктор менеджера коллекции.
     * Выполняет загрузку данных из файла и инициализацию счетчика ID.
     * @param dumpManager Менеджер дампов для работы с файловой системой.
     */
    public CollectionManager(DumpManager dumpManager) {
        this.lastInitTime = null;
        this.lastSaveTime = null;
        this.dumpManager = dumpManager;
        lastID = 100000L;
        loadCollection();
    }

    /**
     * Создает нового дракона и добавляет его в коллекцию.
     * @param name        Имя дракона.
     * @param coordinates Координаты дракона.
     * @param age         Возраст дракона.
     * @param color       Цвет дракона.
     * @param type        Тип дракона.
     * @param character   Характер дракона.
     * @param cave        Пещера дракона.
     */
    public void addToCollection(String name, Coordinates coordinates, Long age,
                                Color color, DragonType type,
                                DragonCharacter character, DragonCave cave) {

        Long newId = generateNextId();
        Date newDate = new Date();

        Dragon newDragon = new Dragon(newId, name,
                coordinates, newDate,
                age, color, type,
                character, cave);
        collection.push(newDragon);
    }

    /**
     * Удаляет последний элемент из коллекции (верхушку стека).
     * @return true, если элемент был удален, иначе false.
     */
    public boolean removeLast() {
        if (collection.isEmpty()) {
            return false;
        }
        collection.pop();
        return true;
    }

    /**
     * Полностью очищает коллекцию.
     */
    public void clearCollection() {
        collection.clear();
    }

    /**
     * Сохраняет текущую коллекцию в файл.
     */
    public void saveCollection() {
        com.google.gson.JsonObject data = new com.google.gson.JsonObject();
        data.addProperty("lastID", lastID); // Кладём ID
        data.add("collection", dumpManager.getGson().toJsonTree(collection));
        dumpManager.writeCollection(data);
        lastSaveTime = new Date();
    }

    /**
     * Загружает коллекцию из файла.
     */
    public void loadCollection() {
        JsonObject data = dumpManager.readCollection();

        if (data != null) {
            // Распаковываем коробку
            this.lastID = data.get("lastID").getAsLong();

            Type type = new TypeToken<Stack<Dragon>>(){}.getType();
            this.collection = dumpManager.getGson().fromJson(data.get("collection"), type);
        } else {
            // Если файла нет или он пустой - начинаем с чистого листа
            this.collection = new Stack<>();
            this.lastID = 100000L;
        }
        this.lastInitTime = new Date();
    }

    /**
     * Генерирует следующий уникальный идентификатор для нового элемента.
     * @return Новый идентификатор.
     */
    public Long generateNextId() {
        return ++lastID;
    }

    /**
     * Ищет дракона в коллекции по его идентификатору.
     * @param id Идентификатор дракона.
     * @return Объект дракона, если найден, иначе null.
     */
    public Dragon getByID(Long id) {
        for (Dragon dragon : collection) {
            if (dragon.getId().equals(id)) {
                return dragon;
            }
        }
        return null;
    }

    /**
     * Проверяет существование дракона в коллекции по его идентификатору.
     * @param id Идентификатор дракона.
     * @return true, если дракон существует, иначе false.
     */
    public boolean isExist(Long id) {
        return getByID(id) != null;
    }

    /**
     * @return Текущий стек коллекции.
     */
    public Stack<Dragon> getCollection() {
        return collection;
    }

    /**
     * @return Дата и время последней инициализации коллекции.
     */
    public Date getLastInitTime() {
        return lastInitTime;
    }

    /**
     * @return Дата и время последнего сохранения коллекции.
     */
    public Date getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * @return Тип используемой коллекции в виде строки.
     */
    public String collectionType() {
        return collection.getClass().getName();
    }

    /**
     * @return Количество элементов в коллекции.
     */
    public int collectionSize() {
        return collection.size();
    }

    /**
     * Сортирует коллекцию в естественном порядке.
     */
    public void sortCollection() {
        collection.sort(null);
    }

}