package ru.itmo.nemat.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * The type Coordinates.
 */
public class Coordinates implements Serializable {
    private double x; //Максимальное значение поля: 543
    private Long y; //Поле не может быть null

    /**
     * Instantiates a new Coordinates.
     *
     * @param x the x
     * @param y the y
     */
    public Coordinates(double x, Long y) {
        setX(x);
        setY(y);
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public Long getY() {
        return y;
    }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(double x) {
        if(x > 543) throw new IllegalArgumentException("Координата х не может быть больше значения 543!");
        this.x = x;
    }

    /**
     * Sets y.
     *
     * @param y the y
     */
    public void setY(Long y) {
        if(y == null) throw new IllegalArgumentException("Координата у не может быть пустой!");
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Double.compare(x, that.x) == 0 && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}