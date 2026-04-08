package ru.itmo.nemat.database;

import ru.itmo.nemat.managers.DatabaseManager;
import ru.itmo.nemat.models.*;
import ru.itmo.nemat.utils.DatabaseAddingResult;
import ru.itmo.nemat.utils.DragonDTO;

import java.sql.*;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Dragon dao.
 */
public class DragonDAO {

    private static final Logger logger = Logger.getLogger(DragonDAO.class.getName());


    /**
     * Add dragon database adding result.
     *
     * @param connection the connection
     * @param dragon     the dragon
     * @param login      the login
     * @return the database adding result
     */
    public DatabaseAddingResult addDragon(Connection connection, DragonDTO dragon, String login) {
        try {
            connection.setAutoCommit(false);
            long coorId = insertCoordinates(connection, dragon.getCoordinates());
            Long caveId = null;
            if (dragon.getCave() != null)
                caveId = insertCave(connection, dragon.getCave());

            long userId = getUserIdByLogin(connection, login);
            DatabaseAddingResult result = insertDragonRecord(connection, dragon, coorId, caveId, userId);

            connection.commit();
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при добавлении дракона пользователем " + login, e);            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {
                }
            }
            return null;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private DatabaseAddingResult insertDragonRecord(Connection connection, DragonDTO dragon, Long coordId, Long caveId, Long userId) throws SQLException {
        String query = "INSERT INTO dragons (name, coordinates, age, color, dragon_type, dragon_character, dragon_cave, user_id)"
                + " VALUES (?, ?, ?, ?::colors, ?::dragon_types, ?::dragon_characters, ?, ?)";
        String[] columnsToReturn = {"id", "creation_date"};
        try (PreparedStatement pstmt = connection.prepareStatement(query, columnsToReturn)) {
            pstmt.setString(1, dragon.getName());
            pstmt.setLong(2, coordId);
            pstmt.setLong(3, dragon.getAge());
            pstmt.setString(4, dragon.getColor().toString());
            pstmt.setString(5, (dragon.getType() != null) ? dragon.getType().name() : null);
            pstmt.setString(6, (dragon.getCharacter() != null) ? dragon.getCharacter().name() : null);
            pstmt.setObject(7, caveId, java.sql.Types.INTEGER);
            pstmt.setLong(8, userId);

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new DatabaseAddingResult(rs.getLong(1), new java.util.Date(rs.getTimestamp(2).getTime()));
                } else {
                    throw new SQLException("Создание дракона провалилось, ID не получен");
                }
            }
        }
    }

    private long getUserIdByLogin(Connection connection, String login) throws SQLException {
        String query = "SELECT id FROM app_users WHERE login = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, login);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new SQLException("Юзер с таким логином не найден");
                }
            }

        }
    }

    private long insertCoordinates(Connection connection, Coordinates coordinates) throws SQLException {
        String query = "INSERT INTO coordinates (x, y) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDouble(1, coordinates.getX());
            pstmt.setDouble(2, coordinates.getY());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new SQLException("Создание координат провалилось, ID не получен");
                }
            }
        }
    }

    private long insertCave(Connection connection, DragonCave cave) throws SQLException {
        String query = "INSERT INTO dragon_caves(depth, number_of_treasures) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            if (cave.getDepth() != null) {
                pstmt.setDouble(1, cave.getDepth());
            } else {
                pstmt.setNull(1, Types.DOUBLE);
            }
            pstmt.setObject(2, cave.getNumberOfTreasures(), Types.INTEGER);

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new SQLException("Создание пещеры не удалось, ID не получен");
                }
            }
        }
    }

    /**
     * Gets dragons.
     *
     * @param connection the connection
     * @return the dragons
     * @throws SQLException the sql exception
     */
    public Stack<Dragon> getDragons(Connection connection) throws SQLException {

        Stack<Dragon> dragons = new Stack<>();
        String query = "SELECT d.id AS dragon_id, d.name AS dragon_name, d.age, d.color, d.dragon_type, d.dragon_character, d.creation_date, c.x, c.y, dc.depth, dc.number_of_treasures, au.login AS owner_login " +
                "FROM dragons d " +
                "JOIN coordinates c ON d.coordinates = c.id " +
                "LEFT JOIN dragon_caves dc ON d.dragon_cave = dc.id " +
                "JOIN app_users au ON d.user_id = au.id";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Long dragonId = rs.getLong("dragon_id");
                    String dragonName = rs.getString("dragon_name");
                    Long dragonAge = rs.getLong("age");
                    Color dragonColor = Color.valueOf(rs.getString("color"));
                    DragonType dragonType = rs.getString("dragon_type") == null ? null : DragonType.valueOf(rs.getString("dragon_type"));
                    DragonCharacter dragonCharacter = rs.getString("dragon_character") == null ? null : DragonCharacter.valueOf(rs.getString("dragon_character"));
                    java.util.Date creationDate = new java.util.Date(rs.getTimestamp("creation_date").getTime());

                    double x = rs.getDouble("x");
                    Long y = rs.getLong("y");
                    Coordinates coord = new Coordinates(x, y);

                    long tempDepth = rs.getLong("depth");
                    Long depth = rs.wasNull() ? null : tempDepth;

                    Integer numberOfTreasures = rs.getObject("number_of_treasures", Integer.class);
                    DragonCave dragonCave = null;

                    String ownerLogin = rs.getString("owner_login");
                    if (depth != null || numberOfTreasures != null)
                        dragonCave = new DragonCave(depth, numberOfTreasures);
                    Dragon dragon = new Dragon(dragonId, dragonName, coord, creationDate, dragonAge, dragonColor, dragonType, dragonCharacter, dragonCave, ownerLogin);
                    dragons.add(dragon);
                }
            }
        }
        logger.info("Из БД успешно загружено драконов: " + dragons.size());
        return dragons;
    }

    /**
     * Delete dragon by id response status.
     *
     * @param connection the connection
     * @param dragonId   the dragon id
     * @param ownerLogin the owner login
     * @return the response status
     * @throws SQLException the sql exception
     */
    public ResponseStatus deleteDragonById(Connection connection, Long dragonId, String ownerLogin) throws SQLException {

        Long coorId;
        Long caveId;

        String selectQuery = "SELECT d.coordinates, d.dragon_cave, au.login AS owner_login " +
                "FROM dragons d " +
                "JOIN app_users au ON d.user_id = au.id " +
                "WHERE d.id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setLong(1, dragonId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    logger.warning("Ошибка: дракона с ID=" + dragonId + " не существует.");
                    return ResponseStatus.NOT_FOUND;
                }

                String actualOwner = rs.getString("owner_login");
                if (!actualOwner.equals(ownerLogin)) {
                    logger.warning("Отказ в доступе: пользователь " + ownerLogin + " пытается удалить чужого дракона (ID=" + dragonId + ")");
                    return ResponseStatus.PERMISSION_DENIED;
                }

                coorId = rs.getLong("coordinates");
                long tempCaveId = rs.getLong("dragon_cave");
                caveId = rs.wasNull() ? null : tempCaveId;
            }
        }

        try {
            connection.setAutoCommit(false);
            String deleteDragonQuery = "Delete from dragons where id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteDragonQuery)) {
                pstmt.setLong(1, dragonId);
                pstmt.executeUpdate();
            }

            String deleteCoordQuery = "Delete from coordinates where id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteCoordQuery)) {
                pstmt.setLong(1, coorId);
                pstmt.executeUpdate();
            }

            if (caveId != null) {
                String deleteCaveQuery = "Delete from dragon_caves where id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(deleteCaveQuery)) {
                    pstmt.setLong(1, caveId);
                    pstmt.executeUpdate();
                }
            }

            connection.commit();
            logger.info("Дракон с ID=" + dragonId + " успешно удален пользователем " + ownerLogin);
            return ResponseStatus.OK;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка БД при удалении дракона ID=" + dragonId + " пользователем " + ownerLogin, e);
            connection.rollback();
            return ResponseStatus.ERROR;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * Update dragon response status.
     *
     * @param connection the connection
     * @param dragonId   the dragon id
     * @param ownerLogin the owner login
     * @param dragon     the dragon
     * @return the response status
     * @throws SQLException the sql exception
     */
    public ResponseStatus updateDragon(Connection connection, Long dragonId, String ownerLogin, DragonDTO dragon) throws SQLException {
        Long coorId;
        Long caveId;
        Long oldCaveIdToDelete = null; // Переменная для отложенного удаления пещеры

        String selectQuery = "SELECT d.coordinates, d.dragon_cave, au.login AS owner_login " +
                "FROM dragons d " +
                "JOIN app_users au ON d.user_id = au.id " +
                "WHERE d.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setLong(1, dragonId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    logger.warning("Ошибка: дракона для обновления с ID=" + dragonId + " не существует.");
                    return ResponseStatus.NOT_FOUND;
                }

                String actualOwner = rs.getString("owner_login");
                if (!actualOwner.equals(ownerLogin)) {
                    logger.warning("Отказ в доступе: обновление чужого дракона (ID=" + dragonId + ")");
                    return ResponseStatus.PERMISSION_DENIED;
                }

                coorId = rs.getLong("coordinates");
                long tempCaveId = rs.getLong("dragon_cave");
                caveId = rs.wasNull() ? null : tempCaveId;
            }
        }

        try {
            connection.setAutoCommit(false);

            String updateCoordsQuery = "UPDATE coordinates SET x = ?, y = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateCoordsQuery)) {
                pstmt.setDouble(1, dragon.getCoordinates().getX());
                pstmt.setDouble(2, dragon.getCoordinates().getY());
                pstmt.setLong(3, coorId);
                pstmt.executeUpdate();
            }

            if (caveId == null && dragon.getCave() != null) {
                caveId = insertCave(connection, dragon.getCave());
            } else if (caveId != null && dragon.getCave() == null) {
                oldCaveIdToDelete = caveId;
                caveId = null;
            } else if (caveId != null && dragon.getCave() != null) {
                String updateCaveQuery = "UPDATE dragon_caves SET depth = ?, number_of_treasures = ? WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateCaveQuery)) {
                    if (dragon.getCave().getDepth() != null) {
                        pstmt.setDouble(1, dragon.getCave().getDepth());
                    } else {
                        pstmt.setNull(1, Types.DOUBLE);
                    }
                    pstmt.setObject(2, dragon.getCave().getNumberOfTreasures(), Types.INTEGER);
                    pstmt.setLong(3, caveId);
                    pstmt.executeUpdate();
                }
            }

            String updateDragonQuery = "UPDATE dragons SET name = ?, age = ?, color = ?::colors, dragon_type = ?::dragon_types, dragon_character = ?::dragon_characters, dragon_cave = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateDragonQuery)) {
                pstmt.setString(1, dragon.getName());
                pstmt.setLong(2, dragon.getAge());
                pstmt.setString(3, dragon.getColor().toString());
                pstmt.setString(4, (dragon.getType() != null) ? dragon.getType().name() : null);
                pstmt.setString(5, (dragon.getCharacter() != null) ? dragon.getCharacter().name() : null);
                pstmt.setObject(6, caveId, java.sql.Types.INTEGER);
                pstmt.setLong(7, dragonId);
                pstmt.executeUpdate();
            }

            if (oldCaveIdToDelete != null) {
                String deleteCaveQuery = "DELETE FROM dragon_caves WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(deleteCaveQuery)) {
                    pstmt.setLong(1, oldCaveIdToDelete);
                    pstmt.executeUpdate();
                }
            }

            connection.commit();
            logger.info("Дракон с ID=" + dragonId + " успешно обновлен.");
            return ResponseStatus.OK;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка БД при обновлении дракона ID=" + dragonId, e);
            connection.rollback();
            return ResponseStatus.ERROR;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * Clear user collection response status.
     *
     * @param connection the connection
     * @param ownerLogin the owner login
     * @return the response status
     * @throws SQLException the sql exception
     */
    public ResponseStatus clearUserCollection(Connection connection, String ownerLogin) throws SQLException {
        try {
            connection.setAutoCommit(false);
            String deleteDragonQuery = "Delete from dragons where user_id = (SELECT id FROM app_users WHERE login = ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteDragonQuery)) {
                pstmt.setString(1, ownerLogin);
                pstmt.executeUpdate();
            }

            String deleteCoordsQuery = "Delete from coordinates where id not in (select coordinates from dragons where coordinates is not null)";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteCoordsQuery)) {
                pstmt.executeUpdate();
            }

            String deleteCaveQuery = "Delete from dragon_caves where id not in (select dragon_cave from dragons where dragon_cave is not null)";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteCaveQuery)) {
                pstmt.executeUpdate();
            }
            connection.commit();
            return ResponseStatus.OK;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка БД при очистке коллекции пользователя " + ownerLogin, e);
            connection.rollback();
            return ResponseStatus.ERROR;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * Delete dragons by character response status.
     *
     * @param connection the connection
     * @param character  the character
     * @param ownerLogin the owner login
     * @return the response status
     * @throws SQLException the sql exception
     */
    public ResponseStatus deleteDragonsByCharacter(Connection connection, DragonCharacter character, String ownerLogin) throws SQLException {

        try {
            connection.setAutoCommit(false);
            String deleteDragonQuery = "Delete from dragons where dragon_character = ?::dragon_characters and user_id = (select id from app_users where login = ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteDragonQuery)) {
                pstmt.setString(1, character.name());
                pstmt.setString(2, ownerLogin);
                pstmt.executeUpdate();
            }

            String deleteCoordsQuery = "Delete from coordinates where id not in (select coordinates from dragons where coordinates is not null)";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteCoordsQuery)) {
                pstmt.executeUpdate();
            }

            String deleteCaveQuery = "Delete from dragon_caves where id not in (select dragon_cave from dragons where dragon_cave is not null)";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteCaveQuery)) {
                pstmt.executeUpdate();
            }
            connection.commit();
            return ResponseStatus.OK;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка БД при удалении драконов по характеру пользователем " + ownerLogin, e);
            connection.rollback();
            return ResponseStatus.ERROR;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        }
    }
}

