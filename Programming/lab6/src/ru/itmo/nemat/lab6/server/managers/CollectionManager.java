package ru.itmo.nemat.lab6.server.managers;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import ru.itmo.nemat.lab6.common.models.*;
import ru.itmo.nemat.lab6.common.utils.DragonDTO;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Logger;

public class CollectionManager {
    private static final Logger logger = Logger.getLogger(CollectionManager.class.getName());

    private final DumpManager dumpManager;
    private Stack<Dragon> collection = new Stack<>();
    private Date lastInitTime;
    private Date lastSaveTime;
    private Long lastID;

    public CollectionManager(DumpManager dumpManager) {
        this.dumpManager = dumpManager;
        this.lastInitTime = null;
        this.lastSaveTime = null;
        this.lastID = 100000L;
        loadCollection();
    }

    public void addToCollection(DragonDTO dragonDTO) {
        Long newID = generateNextId();
        Date newDate = new Date();
        Dragon newDragon = new Dragon(newID, dragonDTO.getName(), dragonDTO.getCoordinates(),
                newDate, dragonDTO.getAge(), dragonDTO.getColor(),
                dragonDTO.getType(), dragonDTO.getCharacter(), dragonDTO.getCave());

        collection.push(newDragon);
        logger.info("Добавлен новый дракон: ID=" + newID);
    }

    public boolean removeLast() {
        if (collection.isEmpty()) {
            return false;
        }
        collection.pop();
        logger.info("Удален последний элемент коллекции.");
        return true;
    }

    public void clearCollection() {
        collection.clear();
        logger.info("Коллекция очищена.");
    }

    public void saveCollection() {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("lastID", lastID);
            data.add("collection", dumpManager.getGson().toJsonTree(collection));
            if(dumpManager.writeCollection(data)) {
                lastSaveTime = new Date();
                logger.info("Коллекция успешно сохранена в файл.");
            }

        } catch (Exception e) {
            logger.severe("Ошибка при сохранении коллекции: " + e.getMessage());
        }
    }

    public void loadCollection() {
        try {
            JsonObject data = dumpManager.readCollection();
            if (data != null) {
                this.lastID = data.get("lastID").getAsLong();
                Type type = new TypeToken<Stack<Dragon>>() {}.getType();
                this.collection = dumpManager.getGson().fromJson(data.get("collection"), type);
                logger.info("Коллекция загружена. Элементов: " + collection.size());
            } else {
                this.collection = new Stack<>();
                this.lastID = 100000L;
                logger.info("Файл пуст или не найден. Создана новая коллекция.");
            }
            this.lastInitTime = new Date();
        } catch (Exception e) {
            logger.severe("Ошибка при загрузке коллекции: " + e.getMessage());
            this.collection = new Stack<>();
            this.lastID = 100000L;
        }
    }

    private Long generateNextId() {
        return ++lastID;
    }

    public Dragon getByID(Long id) {
        return collection.stream()
                .filter(dragon -> dragon.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean isExist(Long id) {
        return collection.stream().anyMatch(dragon -> dragon.getId().equals(id));
    }

    public Stack<Dragon> getCollection() {
        return collection;
    }

    public Date getLastInitTime() {
        return lastInitTime;
    }

    public Date getLastSaveTime() {
        return lastSaveTime;
    }

    public String collectionType() {
        return collection.getClass().getName();
    }

    public int collectionSize() {
        return collection.size();
    }

    public void sortCollection() {
        collection.sort(null);
        logger.info("Коллекция отсортирована.");
    }

    public void updateByID(long id, DragonDTO dto) {
        collection.stream()
                .filter(dragon -> dragon.getId().equals(id))
                .findFirst()
                .ifPresent(dragon -> {
                    dragon.setName(dto.getName());
                    dragon.setCoordinates(dto.getCoordinates());
                    dragon.setAge(dto.getAge());
                    dragon.setColor(dto.getColor());
                    dragon.setType(dto.getType());
                    dragon.setCharacter(dto.getCharacter());
                    dragon.setCave(dto.getCave());
                    logger.info("Дракон с ID=" + id + " успешно обновлен.");
                });
    }
}