package ru.itmo.nemat.utils;

import ru.itmo.nemat.models.*;

import java.io.Serializable;
import java.util.Date;


/**
 * The type Dragon dto.
 */
public class DragonDTO implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Long age; //Значение поля должно быть больше 0, Поле не может быть null
    private Color color; //Поле не может быть null
    private DragonType type; //Поле может быть null
    private DragonCharacter character; //Поле может быть null
    private DragonCave cave; //Поле может быть null

    /**
     * Instantiates a new Dragon dto.
     *
     * @param name        the name
     * @param coordinates the coordinates
     * @param age         the age
     * @param color       the color
     * @param type        the type
     * @param character   the character
     * @param cave        the cave
     */
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


    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        if(name == null || name.isEmpty()) throw new IllegalArgumentException("Имя не может быть пустым!");
        this.name = name;
    }

    /**
     * Sets coordinates.
     *
     * @param coordinates the coordinates
     */
    public void setCoordinates(Coordinates coordinates) {
        if(coordinates == null) throw new IllegalArgumentException("Координаты не могут быть пустыми!");
        this.coordinates = coordinates;
    }

    /**
     * Sets age.
     *
     * @param age the age
     */
    public void setAge(Long age) {
        if(age == null) throw new IllegalArgumentException("Возраст не может быть пустым!");
        if(age <= 0) throw new IllegalArgumentException("Возраст не может быть отрицательным!");
        this.age = age;
    }

    /**
     * Sets color.
     *
     * @param color the color
     */
    public void setColor(Color color) {
        if(color == null) throw new IllegalArgumentException("Цвет не может быть пустым!");
        this.color = color;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(DragonType type) {
        this.type = type;
    }

    /**
     * Sets character.
     *
     * @param character the character
     */
    public void setCharacter(DragonCharacter character) {
        this.character = character;
    }

    /**
     * Sets cave.
     *
     * @param cave the cave
     */
    public void setCave(DragonCave cave) {
        this.cave = cave;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets coordinates.
     *
     * @return the coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Gets age.
     *
     * @return the age
     */
    public Long getAge() {
        return age;
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public DragonType getType() {
        return type;
    }

    /**
     * Gets character.
     *
     * @return the character
     */
    public DragonCharacter getCharacter() {
        return character;
    }

    /**
     * Gets cave.
     *
     * @return the cave
     */
    public DragonCave getCave() {
        return cave;
    }



}