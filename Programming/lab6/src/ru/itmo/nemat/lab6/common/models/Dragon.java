package ru.itmo.nemat.lab6.common.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Dragon implements Comparable<Dragon>, Serializable {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long age; //Значение поля должно быть больше 0, Поле не может быть null
    private Color color; //Поле не может быть null
    private DragonType type; //Поле может быть null
    private DragonCharacter character; //Поле может быть null
    private DragonCave cave; //Поле может быть null

    public Dragon(Long id, String name, Coordinates coordinates,
                  Date creationDate, Long age,
                  Color color, DragonType type,
                  DragonCharacter character, DragonCave cave) {
        if (id == null || id <= 0) throw new IllegalArgumentException("ID не может быть пустым или отрицательным!");
        if (creationDate == null) throw new IllegalArgumentException("Дата создания не может быть пустой!");
        this.id = id;
        this.creationDate = creationDate;
        initializeFields(name, coordinates, age, color, type, character, cave);
    }

    private void initializeFields(String name, Coordinates coordinates,
                                  Long age, Color color, DragonType type,
                                  DragonCharacter character, DragonCave cave) {
        setName(name);
        setCoordinates(coordinates);
        setAge(age);
        setColor(color);
        setType(type);
        setCharacter(character);
        setCave(cave);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Имя не может быть пустым!");
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("Координаты не могут быть пустыми!");
        this.coordinates = coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        if (age == null) throw new IllegalArgumentException("Возраст не может быть пустым!");
        if (age <= 0) throw new IllegalArgumentException("Возраст не может быть отрицательным!");
        this.age = age;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        if (color == null) throw new IllegalArgumentException("Цвет не может быть пустым!");
        this.color = color;
    }

    public DragonType getType() {
        return type;
    }

    public void setType(DragonType type) {
        this.type = type;
    }

    public DragonCharacter getCharacter() {
        return character;
    }

    public void setCharacter(DragonCharacter character) {
        this.character = character;
    }

    public DragonCave getCave() {
        return cave;
    }

    public void setCave(DragonCave cave) {
        this.cave = cave;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dragon dragon = (Dragon) o;
        return Objects.equals(id, dragon.id) && Objects.equals(name, dragon.name) && Objects.equals(coordinates, dragon.coordinates) && Objects.equals(creationDate, dragon.creationDate) && Objects.equals(age, dragon.age) && color == dragon.color && type == dragon.type && character == dragon.character && Objects.equals(cave, dragon.cave);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, age, color, type, character, cave);
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String formattedDate = dateFormat.format(creationDate);
        return "Dragon (ID=" + id + "):\n" +
                "  Имя: " + name + "\n" +
                "  Координаты: " + coordinates + "\n" +
                "  Дата создания: " + formattedDate + "\n" +
                "  Возраст: " + age + "\n" +
                "  Цвет: " + color + "\n" +
                "  Тип: " + type + "\n" +
                "  Характер: " + character + "\n" +
                "  Пещера: " + cave + "\n";
    }

    @Override
    public int compareTo(Dragon other) {
        if (other == null) return 1;
        return this.name.compareTo(other.getName());
    }
}