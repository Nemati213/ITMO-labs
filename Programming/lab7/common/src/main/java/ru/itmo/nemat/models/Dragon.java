package ru.itmo.nemat.models;

import ru.itmo.nemat.utils.DragonDTO;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * The type Dragon.
 */
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
    /**
     * The Owner login.
     */
    public String ownerLogin;

    /**
     * Instantiates a new Dragon.
     *
     * @param id           the id
     * @param name         the name
     * @param coordinates  the coordinates
     * @param creationDate the creation date
     * @param age          the age
     * @param color        the color
     * @param type         the type
     * @param character    the character
     * @param cave         the cave
     * @param ownerLogin   the owner login
     */
    public Dragon(Long id, String name, Coordinates coordinates,
                  Date creationDate, Long age,
                  Color color, DragonType type,
                  DragonCharacter character, DragonCave cave,  String ownerLogin) {
        if (id == null || id <= 0) throw new IllegalArgumentException("ID не может быть пустым или отрицательным!");
        if (creationDate == null) throw new IllegalArgumentException("Дата создания не может быть пустой!");
        this.id = id;
        this.creationDate = creationDate;
        initializeFields(name, coordinates, age, color, type, character, cave,  ownerLogin);
    }

    /**
     * Instantiates a new Dragon.
     *
     * @param id           the id
     * @param dragonDTO    the dragon dto
     * @param creationDate the creation date
     * @param ownerLogin   the owner login
     */
    public Dragon(Long id, DragonDTO dragonDTO, Date creationDate, String ownerLogin) {
        this(id, dragonDTO.getName(), dragonDTO.getCoordinates(), creationDate,
                dragonDTO.getAge(), dragonDTO.getColor(), dragonDTO.getType(),
                dragonDTO.getCharacter(), dragonDTO.getCave(), ownerLogin);
    }
    private void initializeFields(String name, Coordinates coordinates,
                                  Long age, Color color, DragonType type,
                                  DragonCharacter character, DragonCave cave, String ownerLogin) {
        setName(name);
        setCoordinates(coordinates);
        setAge(age);
        setColor(color);
        setType(type);
        setCharacter(character);
        setCave(cave);
        setOwnerLogin(ownerLogin);
    }

    /**
     * Gets owner login.
     *
     * @return the owner login
     */
    public String getOwnerLogin() {
        return ownerLogin;
    }


    /**
     * Sets owner login.
     *
     * @param ownerLogin the owner login
     */
    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
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
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Имя не может быть пустым!");
        this.name = name;
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
     * Sets coordinates.
     *
     * @param coordinates the coordinates
     */
    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("Координаты не могут быть пустыми!");
        this.coordinates = coordinates;
    }

    /**
     * Gets creation date.
     *
     * @return the creation date
     */
    public Date getCreationDate() {
        return creationDate;
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
     * Sets age.
     *
     * @param age the age
     */
    public void setAge(Long age) {
        if (age == null) throw new IllegalArgumentException("Возраст не может быть пустым!");
        if (age <= 0) throw new IllegalArgumentException("Возраст не может быть отрицательным!");
        this.age = age;
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
     * Sets color.
     *
     * @param color the color
     */
    public void setColor(Color color) {
        if (color == null) throw new IllegalArgumentException("Цвет не может быть пустым!");
        this.color = color;
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
     * Sets type.
     *
     * @param type the type
     */
    public void setType(DragonType type) {
        this.type = type;
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
     * Sets character.
     *
     * @param character the character
     */
    public void setCharacter(DragonCharacter character) {
        this.character = character;
    }

    /**
     * Gets cave.
     *
     * @return the cave
     */
    public DragonCave getCave() {
        return cave;
    }

    /**
     * Sets cave.
     *
     * @param cave the cave
     */
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