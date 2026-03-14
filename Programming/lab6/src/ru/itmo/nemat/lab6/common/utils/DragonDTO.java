package ru.itmo.nemat.lab6.common.utils;

import ru.itmo.nemat.lab6.common.models.*;

import java.io.Serializable;


public class DragonDTO implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Long age; //Значение поля должно быть больше 0, Поле не может быть null
    private Color color; //Поле не может быть null
    private DragonType type; //Поле может быть null
    private DragonCharacter character; //Поле может быть null
    private DragonCave cave; //Поле может быть null


    public DragonDTO(String name, Coordinates coordinates, Long age,
                     Color color, DragonType type,
                     DragonCharacter character, DragonCave cave)
    {
        initializeFields(name, coordinates, age, color, type, character, cave);
    }


    private void initializeFields(String name, Coordinates coordinates,
                                  Long age, Color color, DragonType type,
                                  DragonCharacter character, DragonCave cave)
    {
        setName(name);
        setCoordinates(coordinates);
        setAge(age);
        setColor(color);
        setType(type);
        setCharacter(character);
        setCave(cave);
    }


    public void setName(String name) {
        if(name == null || name.isEmpty()) throw new IllegalArgumentException("Имя не может быть пустым!");
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        if(coordinates == null) throw new IllegalArgumentException("Координаты не могут быть пустыми!");
        this.coordinates = coordinates;
    }

    public void setAge(Long age) {
        if(age == null) throw new IllegalArgumentException("Возраст не может быть пустым!");
        if(age <= 0) throw new IllegalArgumentException("Возраст не может быть отрицательным!");
        this.age = age;
    }

    public void setColor(Color color) {
        if(color == null) throw new IllegalArgumentException("Цвет не может быть пустым!");
        this.color = color;
    }

    public void setType(DragonType type) {
        this.type = type;
    }

    public void setCharacter(DragonCharacter character) {
        this.character = character;
    }

    public void setCave(DragonCave cave) {
        this.cave = cave;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Long getAge() {
        return age;
    }

    public Color getColor() {
        return color;
    }

    public DragonType getType() {
        return type;
    }

    public DragonCharacter getCharacter() {
        return character;
    }

    public DragonCave getCave() {
        return cave;
    }

}