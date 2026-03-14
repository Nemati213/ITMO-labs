package ru.itmo.nemat.lab5.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс координат. Хранит местоположение объекта в двумерном пространстве.
 *
 * @author Nemati213
 */
public class Coordinates implements Serializable {
    private double x; //Максимальное значение поля: 543
    private Long y; //Поле не может быть null

    /**
     * Конструктор для создания объекта координат.
     * @param x Координата X.
     * @param y Координата Y.
     */
    public Coordinates(double x, Long y) {
        setX(x);
        setY(y);
    }

    /**
     * @return Координата X.
     */
    public double getX() {
        return x;
    }

    /**
     * @return Координата Y.
     */
    public Long getY() {
        return y;
    }

    /**
     * Устанавливает координату X.
     * @param x Координата X (максимальное значение: 543).
     */
    public void setX(double x) {
        if(x > 543) throw new IllegalArgumentException("Координата х не может быть больше значения 543!");
        this.x = x;
    }

    /**
     * Устанавливает координату Y.
     * @param y Координата Y (не может быть null).
     */
    public void setY(Long y) {
        if(y == null) throw new IllegalArgumentException("Координата у не может быть пустой!");
        this.y = y;
    }

    /**
     * Сравнивает текущий объект с другим объектом.
     * @param o Объект для сравнения.
     * @return true, если объекты равны, иначе false.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Double.compare(x, that.x) == 0 && Objects.equals(y, that.y);
    }

    /**
     * @return Хеш-код объекта.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * @return Строковое представление координат.
     */
    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}