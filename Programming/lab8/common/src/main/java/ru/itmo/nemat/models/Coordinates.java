package ru.itmo.nemat.models;

import ru.itmo.nemat.exceptions.InvalidArgumentException;

import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable {
    private double x; //Максимальное значение поля: 543
    private Long y; //Поле не может быть null

    public Coordinates(double x, Long y) throws InvalidArgumentException {
        setX(x);
        setY(y);
    }

    public double getX() {
        return x;
    }

    public Long getY() {
        return y;
    }

    public void setX(double x) throws InvalidArgumentException {
        if(x > 200 || x < 0) throw new InvalidArgumentException("Координата х может принимать значения от 0 до 200!");
        this.x = x;
    }

    public void setY(Long y) throws InvalidArgumentException {
        if(y == null) throw new InvalidArgumentException("Координата у не может быть пустой!");
        if(y < 0 || y > 100) throw new InvalidArgumentException("Координата y может принимать значения от 0 до 100!");
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