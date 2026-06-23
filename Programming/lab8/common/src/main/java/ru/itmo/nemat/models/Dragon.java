package ru.itmo.nemat.models;

import ru.itmo.nemat.exceptions.InvalidArgumentException;
import ru.itmo.nemat.utils.DragonDTO;

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
    public String ownerLogin;

    public Dragon(Long id, String name, Coordinates coordinates,
                  Date creationDate, Long age,
                  Color color, DragonType type,
                  DragonCharacter character, DragonCave cave,  String ownerLogin) throws InvalidArgumentException {
        if (id == null || id <= 0) throw new InvalidArgumentException("ID не может быть пустым или отрицательным!");
        if (creationDate == null) throw new InvalidArgumentException("Дата создания не может быть пустой!");
        this.id = id;
        this.creationDate = creationDate;
        initializeFields(name, coordinates, age, color, type, character, cave,  ownerLogin);
    }

    public Dragon(Long id, DragonDTO dragonDTO, Date creationDate, String ownerLogin) throws InvalidArgumentException {
        this(id, dragonDTO.getName(), dragonDTO.getCoordinates(), creationDate,
                dragonDTO.getAge(), dragonDTO.getColor(), dragonDTO.getType(),
                dragonDTO.getCharacter(), dragonDTO.getCave(), ownerLogin);
    }
    private void initializeFields(String name, Coordinates coordinates,
                                  Long age, Color color, DragonType type,
                                  DragonCharacter character, DragonCave cave, String ownerLogin) throws InvalidArgumentException {
        setName(name);
        setCoordinates(coordinates);
        setAge(age);
        setColor(color);
        setType(type);
        setCharacter(character);
        setCave(cave);
        setOwnerLogin(ownerLogin);
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }


    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidArgumentException {
        if (name == null || name.isEmpty()) throw new InvalidArgumentException("Имя не может быть пустым!");
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) throws InvalidArgumentException {
        if (coordinates == null) throw new InvalidArgumentException("Координаты не могут быть пустыми!");
        this.coordinates = coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) throws InvalidArgumentException {
        if (age == null) throw new InvalidArgumentException("Возраст не может быть пустым!");
        if (age <= 0) throw new InvalidArgumentException("Возраст не может быть отрицательным!");
        this.age = age;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) throws InvalidArgumentException {
        if (color == null) throw new InvalidArgumentException("Цвет не может быть пустым!");
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dragon dragon = (Dragon) o;
        return Objects.equals(id, dragon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
                "  Пещера: " + cave + "\n" +
                "  Владелец: " + ownerLogin + "\n";
    }

    @Override
    public int compareTo(Dragon other) {
        if (other == null) return 1;
        if(other == this) return 0;
        return this.name.compareTo(other.getName());
    }
}