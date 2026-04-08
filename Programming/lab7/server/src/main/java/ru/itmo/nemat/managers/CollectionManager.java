package ru.itmo.nemat.managers;

import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.models.DragonCharacter;
import ru.itmo.nemat.utils.DragonDTO;

import java.util.*;
import java.util.logging.Logger;

/**
 * The type Collection manager.
 */
public class CollectionManager {
    private static final Logger logger = Logger.getLogger(CollectionManager.class.getName());

    private final Stack<Dragon> collection = new Stack<>();
    private Date lastInitTime;

    /**
     * Instantiates a new Collection manager.
     */
    public CollectionManager() {
        this.lastInitTime = null;
    }

    /**
     * Load collection.
     *
     * @param collection the collection
     */
    public void loadCollection(Stack<Dragon> collection) {
        synchronized (this.collection) {
            this.collection.clear();
            this.collection.addAll(collection);
            this.lastInitTime = new Date();
            logger.info("Коллекция загружена. Элементов: " + this.collection.size());
        }
    }


    /**
     * Gets by id.
     *
     * @param id the id
     * @return the by id
     */
    public Dragon getByID(Long id) {
        synchronized (collection) {
            return collection.stream()
                    .filter(dragon -> dragon.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }
    }


    /**
     * Clear collection.
     *
     * @param ownerLogin the owner login
     */
    public void clearCollection(String ownerLogin) {
        synchronized (collection) {
            collection.removeIf(dragon -> dragon.getOwnerLogin().equals(ownerLogin));
            logger.info("Удалены элементы пользователя: " + ownerLogin);
        }
    }


    /**
     * Add to collection.
     *
     * @param newDragon the new dragon
     */
    public void addToCollection(Dragon newDragon) {
        synchronized (collection) {
            collection.push(newDragon);
        }
        logger.info("Добавлен новый дракон: ID=" + newDragon.getId());
    }

    /**
     * Remove last boolean.
     *
     * @param ownerLogin the owner login
     * @return the boolean
     */
    public boolean removeLast(String ownerLogin) {
        synchronized (collection) {
            if (collection.isEmpty()) {
                return false;
            }

            for (int i = collection.size() - 1; i >= 0; i--) {
                Dragon currentDragon = collection.get(i);

                if (currentDragon.getOwnerLogin().equals(ownerLogin)) {
                    collection.remove(i); // Удаляем именно его!
                    logger.info("Удален последний дракон пользователя: " + ownerLogin);
                    return true;
                }
            }

            logger.info("У пользователя " + ownerLogin + " нет драконов для удаления.");
            return false;
        }
    }


    /**
     * Is exist boolean.
     *
     * @param id the id
     * @return the boolean
     */
    public boolean isExist(Long id) {
        synchronized (collection) {
            return collection.stream().anyMatch(dragon -> dragon.getId().equals(id));
        }
    }

    /**
     * Gets by id.
     *
     * @param id the id
     * @return the by id
     */
    public Dragon getById(Long id) {
        synchronized (collection) {
            return collection.stream().filter(dragon -> dragon.getId().equals(id)).findFirst().orElse(null);
        }
    }

    /**
     * Gets collection.
     *
     * @return the collection
     */
    public List<Dragon> getCollection() {
        synchronized (collection) {
            return new ArrayList<>(collection);
        }
    }

    /**
     * Gets last init time.
     *
     * @return the last init time
     */
    public Date getLastInitTime() {
        synchronized (collection) {
            return lastInitTime != null?  new Date(lastInitTime.getTime()) :  null;
        }
    }


    /**
     * Collection type string.
     *
     * @return the string
     */
    public String collectionType() {
        return collection.getClass().getName();
    }

    /**
     * Collection size int.
     *
     * @return the int
     */
    public int collectionSize() {
        return collection.size();
    }

    /**
     * Sort collection.
     */
    public void sortCollection() {
        synchronized (collection) {
            collection.sort(null);
            logger.info("Коллекция отсортирована.");
        }
    }

    /**
     * Find last by user long.
     *
     * @param ownerLogin the owner login
     * @return the long
     */
    public Long findLastByUser(String ownerLogin) {
        synchronized (collection) {
            if (collection.isEmpty()) {
                return null;
            }

            for (int i = collection.size() - 1; i >= 0; i--) {
                Dragon currentDragon = collection.get(i);

                if (currentDragon.getOwnerLogin().equals(ownerLogin)) {
                    return currentDragon.getId();
                }
            }
            return null;
        }
    }

    /**
     * Update by id.
     *
     * @param id         the id
     * @param newDragon  the new dragon
     * @param ownerLogin the owner login
     */
    public void updateByID(Long id, DragonDTO newDragon, String ownerLogin) {
        synchronized (collection) {
            collection.stream()
                    .filter(dragon -> dragon.getId().equals(id) && dragon.getOwnerLogin().equals(ownerLogin))
                    .findFirst()
                    .ifPresent(dragon -> {
                        dragon.setName(newDragon.getName());
                        dragon.setCoordinates(newDragon.getCoordinates());
                        dragon.setAge(newDragon.getAge());
                        dragon.setColor(newDragon.getColor());
                        dragon.setType(newDragon.getType());
                        dragon.setCharacter(newDragon.getCharacter());
                        dragon.setCave(newDragon.getCave());
                        logger.info("Дракон с ID=" + id + " успешно обновлен.");
                    });
        }
    }

    /**
     * Remove by character int.
     *
     * @param character  the character
     * @param ownerLogin the owner login
     * @return the int
     */
    public int removeByCharacter(DragonCharacter character, String ownerLogin) {

        synchronized (collection) {
            int sizeBefore = collection.size();
            collection.removeIf(dragon -> dragon.getCharacter() == character &&  dragon.getOwnerLogin().equals(ownerLogin));
            int removedCount = sizeBefore - collection.size();
            if (removedCount > 0) logger.info("Пользователь " + ownerLogin + " удалил элементов: " + removedCount);

            return removedCount;
        }
    }

    /**
     * Remove by id int.
     *
     * @param id         the id
     * @param ownerLogin the owner login
     * @return the int
     */
    public int removeById(Long id, String ownerLogin) {
        synchronized(collection) {
            int sizeBefore = collection.size();
            collection.removeIf(d -> d.getId().equals(id) && d.getOwnerLogin().equals(ownerLogin));
            if (sizeBefore > collection.size()) logger.info("Удален дракон с ID=" + id);
            return sizeBefore - collection.size();

        }
    }
}


