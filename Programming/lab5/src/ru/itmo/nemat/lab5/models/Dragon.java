package ru.itmo.nemat.lab5.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


/**
 * Класс, описывающий дракона. Является основным элементом коллекции.
 * Реализует интерфейс Comparable для сортировки по умолчанию (по имени).
 *
 * @author Nemati213
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
     * Конструктор дракона.
     * @param id Уникальный идентификатор (генерируется автоматически).
     * @param name Имя дракона.
     * @param coordinates Координаты местоположения.
     * @param creationDate Дата создания (генерируется автоматически).
     * @param age Возраст дракона.
     * @param color Цвет дракона.
     * @param type Тип дракона.
     * @param character Характер дракона.
     * @param cave Пещера дракона.
     */
    public Dragon(Long id, String name, Coordinates coordinates,
                  Date creationDate, Long age,
                  Color color, DragonType type,
                  DragonCharacter character, DragonCave cave)
    {
        if(id ==  null || id <= 0) throw new IllegalArgumentException("ID не может быть пустым или отрицательным!");
        if(creationDate == null) throw new IllegalArgumentException("Дата создания не может быть пустой!");
        this.id = id;
        this.creationDate = creationDate;
        initializeFields(name, coordinates, age, color, type, character, cave);
    }

    /**
     * Приватный метод для инициализации полей через сеттеры с валидацией.
     */
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
     * Устанавливает имя дракона.
     * @param name Имя (не может быть пустым или null).
     */
    public void setName(String name) {
        if(name == null || name.isEmpty()) throw new IllegalArgumentException("Имя не может быть пустым!");
        this.name = name;
    }

    /**
     * Устанавливает координаты дракона.
     * @param coordinates Объект координат (не может быть null).
     */
    public void setCoordinates(Coordinates coordinates) {
        if(coordinates == null) throw new IllegalArgumentException("Координаты не могут быть пустыми!");
        this.coordinates = coordinates;
    }

    /**
     * Устанавливает возраст дракона.
     * @param age Возраст (не может быть null, должен быть больше 0).
     */
    public void setAge(Long age) {
        if(age == null) throw new IllegalArgumentException("Возраст не может быть пустым!");
        if(age <= 0) throw new IllegalArgumentException("Возраст не может быть отрицательным!");
        this.age = age;
    }

    /**
     * Устанавливает цвет дракона.
     * @param color Цвет (не может быть null).
     */
    public void setColor(Color color) {
        if(color == null) throw new IllegalArgumentException("Цвет не может быть пустым!");
        this.color = color;
    }

    /**
     * Устанавливает тип дракона.
     * @param type Тип дракона (может быть null).
     */
    public void setType(DragonType type) {
        this.type = type;
    }

    /**
     * Устанавливает характер дракона.
     * @param character Характер (может быть null).
     */
    public void setCharacter(DragonCharacter character) {
        this.character = character;
    }

    /**
     * Устанавливает пещеру дракона.
     * @param cave Пещера (может быть null).
     */
    public void setCave(DragonCave cave) {
        this.cave = cave;
    }

    /**
     * @return Уникальный идентификатор дракона.
     */
    public Long getId() {
        return id;
    }

    /**
     * @return Имя дракона.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Координаты дракона.
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * @return Дата создания дракона.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @return Возраст дракона.
     */
    public Long getAge() {
        return age;
    }

    /**
     * @return Цвет дракона.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return Тип дракона.
     */
    public DragonType getType() {
        return type;
    }

    /**
     * @return Характер дракона.
     */
    public DragonCharacter getCharacter() {
        return character;
    }

    /**
     * @return Пещера дракона.
     */
    public DragonCave getCave() {
        return cave;
    }

    /**
     * Проверяет равенство текущего объекта с другим объектом.
     * @param o Объект для сравнения.
     * @return true, если объекты равны, иначе false.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dragon dragon = (Dragon) o;
        return Objects.equals(id, dragon.id) && Objects.equals(name, dragon.name) && Objects.equals(coordinates, dragon.coordinates) && Objects.equals(creationDate, dragon.creationDate) && Objects.equals(age, dragon.age) && color == dragon.color && type == dragon.type && character == dragon.character && Objects.equals(cave, dragon.cave);
    }

    /**
     * @return Хеш-код объекта.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, age, color, type, character, cave);
    }

    /**
     * @return Строковое представление дракона.
     */
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

    /**
     * Сравнивает текущего дракона с другим по имени.
     * @param other Дракон для сравнения.
     * @return Отрицательное число, ноль или положительное число в зависимости от лексикографического порядка имен.
     */
    @Override
    public int compareTo(Dragon other) {
        if (other == null) return 1;
        return this.name.compareTo(other.getName());
    }
}