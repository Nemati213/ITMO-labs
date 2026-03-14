package ru.itmo.nemat.lab6.common.models;

import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable {
    private double x; //Максимальное значение поля: 543
    private Long y; //Поле не может быть null

    public Coordinates(double x, Long y) {
        setX(x);
        setY(y);
    }

    public double getX() {
        return x;
    }

    public Long getY() {
        return y;
    }

    public void setX(double x) {
        if(x > 543) throw new IllegalArgumentException("Координата х не может быть больше значения 543!");
        this.x = x;
    }

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