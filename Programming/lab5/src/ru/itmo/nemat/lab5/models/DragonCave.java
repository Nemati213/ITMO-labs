package ru.itmo.nemat.lab5.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс пещеры дракона. Хранит информацию о глубине пещеры и количестве сокровищ в ней.
 *
 * @author Nemati213
 */
public class DragonCave implements Serializable {
    private Long depth; //Поле может быть null
    private Integer numberOfTreasures; //Поле может быть null, Значение поля должно быть больше 0

    /**
     * Конструктор для создания объекта пещеры.
     * @param depth Глубина пещеры.
     * @param numberOfTreasures Количество сокровищ.
     */
    public DragonCave(Long depth, Integer numberOfTreasures) {
        setDepth(depth);
        setNumberOfTreasures(numberOfTreasures);
    }

    /**
     * @return Глубина пещеры.
     */
    public Long getDepth() {
        return depth;
    }

    /**
     * @return Количество сокровищ.
     */
    public Integer getNumberOfTreasures() {
        return numberOfTreasures;
    }

    /**
     * Устанавливает глубину пещеры.
     * @param depth Глубина пещеры (может быть null).
     */
    public void setDepth(Long depth) {
        this.depth = depth;
    }

    /**
     * Устанавливает количество сокровищ.
     * @param numberOfTreasures Количество сокровищ (должно быть больше 0 или null).
     */
    public void setNumberOfTreasures(Integer numberOfTreasures) {

        if(numberOfTreasures != null && numberOfTreasures <= 0) {
            throw new IllegalArgumentException("Количество сокровищ должно быть больше 0!");
        }
        this.numberOfTreasures = numberOfTreasures;
    }

    /**
     * Сравнивает текущий объект с другим объектом.
     * @param o Объект для сравнения.
     * @return true, если объекты равны, иначе false.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DragonCave that = (DragonCave) o;
        return Objects.equals(depth, that.depth) && Objects.equals(numberOfTreasures, that.numberOfTreasures);
    }

    /**
     * @return Хеш-код объекта.
     */
    @Override
    public int hashCode() {
        return Objects.hash(depth, numberOfTreasures);
    }

    /**
     * @return Строковое представление пещеры.
     */
    @Override
    public String toString() {
        return "DragonCave{" +
                "depth=" + depth +
                ", numberOfTreasures=" + numberOfTreasures +
                '}';
    }
}