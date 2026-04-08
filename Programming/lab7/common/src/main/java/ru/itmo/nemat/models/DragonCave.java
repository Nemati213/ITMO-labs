package ru.itmo.nemat.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * The type Dragon cave.
 */
public class DragonCave implements Serializable {
    private Long depth; //Поле может быть null
    private Integer numberOfTreasures; //Поле может быть null, Значение поля должно быть больше 0

    /**
     * Instantiates a new Dragon cave.
     *
     * @param depth             the depth
     * @param numberOfTreasures the number of treasures
     */
    public DragonCave(Long depth, Integer numberOfTreasures) {
        setDepth(depth);
        setNumberOfTreasures(numberOfTreasures);
    }

    /**
     * Gets depth.
     *
     * @return the depth
     */
    public Long getDepth() {
        return depth;
    }

    /**
     * Sets depth.
     *
     * @param depth the depth
     */
    public void setDepth(Long depth) {
        this.depth = depth;
    }

    /**
     * Gets number of treasures.
     *
     * @return the number of treasures
     */
    public Integer getNumberOfTreasures() {
        return numberOfTreasures;
    }

    /**
     * Sets number of treasures.
     *
     * @param numberOfTreasures the number of treasures
     */
    public void setNumberOfTreasures(Integer numberOfTreasures) {

        if (numberOfTreasures != null && numberOfTreasures <= 0) {
            throw new IllegalArgumentException("Количество сокровищ должно быть больше 0!");
        }
        this.numberOfTreasures = numberOfTreasures;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DragonCave that = (DragonCave) o;
        return Objects.equals(depth, that.depth) && Objects.equals(numberOfTreasures, that.numberOfTreasures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(depth, numberOfTreasures);
    }

    @Override
    public String toString() {
        return "DragonCave{" +
                "depth=" + depth +
                ", numberOfTreasures=" + numberOfTreasures +
                '}';
    }
}