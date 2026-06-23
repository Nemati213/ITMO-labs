package ru.itmo.nemat.database;

import ru.itmo.nemat.models.*;
import ru.itmo.nemat.utils.DatabaseAddingResult;
import ru.itmo.nemat.utils.DragonDTO;

import java.sql.*;
import java.util.List;
import java.util.Stack;

public class DragonDAO {

    private static final String INSERT_COORD = "INSERT INTO coordinates (x, y) VALUES (?, ?)";
    private static final String INSERT_CAVE = "INSERT INTO dragon_caves(depth, number_of_treasures) VALUES (?, ?)";
    private static final String INSERT_DRAGON = "INSERT INTO dragons (name, coordinates, age, color, dragon_type, dragon_character, dragon_cave, user_id) VALUES (?, ?, ?, ?::colors, ?::dragon_types, ?::dragon_characters, ?, ?)";

    private static final String SELECT_ALL_DRAGONS = "SELECT d.id AS dragon_id, d.name AS dragon_name, d.age, d.color, d.dragon_type, d.dragon_character, d.creation_date, c.x, c.y, dc.depth, dc.number_of_treasures, au.login AS owner_login FROM dragons d JOIN coordinates c ON d.coordinates = c.id LEFT JOIN dragon_caves dc ON d.dragon_cave = dc.id JOIN app_users au ON d.user_id = au.id";
    private static final String SELECT_DRAGON_LINKS = "SELECT user_id, coordinates, dragon_cave FROM dragons WHERE id = ?";

    private static final String DELETE_DRAGON = "DELETE FROM dragons WHERE id = ?";
    private static final String DELETE_COORD = "DELETE FROM coordinates WHERE id = ?";
    private static final String DELETE_CAVE = "DELETE FROM dragon_caves WHERE id = ?";
    private static final String DELETE_BY_USER = "DELETE FROM dragons WHERE user_id = ?";
    private static final String DELETE_BY_CHAR = "DELETE FROM dragons WHERE dragon_character = ?::dragon_characters AND user_id = ?";

    private static final String DELETE_ORPHAN_COORDS = "DELETE FROM coordinates WHERE id NOT IN (SELECT coordinates FROM dragons WHERE coordinates IS NOT NULL)";
    private static final String DELETE_ORPHAN_CAVES = "DELETE FROM dragon_caves WHERE id NOT IN (SELECT dragon_cave FROM dragons WHERE dragon_cave IS NOT NULL)";

    private static final String UPDATE_COORD = "UPDATE coordinates SET x = ?, y = ? WHERE id = ?";
    private static final String UPDATE_CAVE = "UPDATE dragon_caves SET depth = ?, number_of_treasures = ? WHERE id = ?";
    private static final String UPDATE_DRAGON = "UPDATE dragons SET name = ?, age = ?, color = ?::colors, dragon_type = ?::dragon_types, dragon_character = ?::dragon_characters, dragon_cave = ? WHERE id = ?";

    private static final String SELECT_BASE = "SELECT d.id AS dragon_id, d.name AS dragon_name, d.age, d.color, d.dragon_type, d.dragon_character, d.creation_date, c.x, c.y, dc.depth, dc.number_of_treasures, au.login AS owner_login FROM dragons d JOIN coordinates c ON d.coordinates = c.id LEFT JOIN dragon_caves dc ON d.dragon_cave = dc.id JOIN app_users au ON d.user_id = au.id";

    public static class DragonLinks {
        public final long userId;
        public final long coordId;
        public final Long caveId;

        public DragonLinks(long userId, long coordId, Long caveId) {
            this.userId = userId;
            this.coordId = coordId;
            this.caveId = caveId;
        }
    }

    public long insertCoordinates(Connection connection, Coordinates coordinates) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_COORD, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDouble(1, coordinates.getX());
            pstmt.setDouble(2, coordinates.getY());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                throw new SQLException("error.database.coord_id");            }
        }
    }

    public Dragon getDragonById(Connection connection, long id) throws SQLException {
        String sql = SELECT_BASE + " WHERE d.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Dragon> list = extractDragons(rs);
                return list.isEmpty() ? null : list.get(0);
            }
        }
    }

    public List<Dragon> getDragonsByUserId(Connection connection, long userId) throws SQLException {
        String sql = SELECT_BASE + " WHERE d.user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return extractDragons(rs);
            }
        }
    }

    public List<Dragon> getDragonsByCharacterAndUser(Connection connection, DragonCharacter character, long userId) throws SQLException {
        String sql = SELECT_BASE + " WHERE d.dragon_character = ?::dragon_characters AND d.user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, character.name());
            pstmt.setLong(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return extractDragons(rs);
            }
        }
    }

    private List<Dragon> extractDragons(ResultSet rs) throws SQLException {
        java.util.List<Dragon> dragons = new java.util.ArrayList<>();
        while (rs.next()) {
            Long dragonId = rs.getLong("dragon_id");
            String dragonName = rs.getString("dragon_name");
            Long dragonAge = rs.getLong("age");
            Color dragonColor = Color.valueOf(rs.getString("color"));
            DragonType dragonType = rs.getString("dragon_type") == null ? null : DragonType.valueOf(rs.getString("dragon_type"));
            DragonCharacter dragonCharacter = rs.getString("dragon_character") == null ? null : DragonCharacter.valueOf(rs.getString("dragon_character"));
            java.util.Date creationDate = new java.util.Date(rs.getTimestamp("creation_date").getTime());
            Coordinates coord = new Coordinates(rs.getDouble("x"), rs.getLong("y"));
            long tempDepth = rs.getLong("depth");
            Long depth = rs.wasNull() ? null : tempDepth;
            Integer numberOfTreasures = rs.getObject("number_of_treasures", Integer.class);
            DragonCave dragonCave = (depth != null || numberOfTreasures != null) ? new DragonCave(depth, numberOfTreasures) : null;
            String ownerLogin = rs.getString("owner_login");
            dragons.add(new Dragon(dragonId, dragonName, coord, creationDate, dragonAge, dragonColor, dragonType, dragonCharacter, dragonCave, ownerLogin));
        }
        return dragons;
    }

    public long insertCave(Connection connection, DragonCave cave) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_CAVE, Statement.RETURN_GENERATED_KEYS)) {
            if (cave.getDepth() != null) pstmt.setDouble(1, cave.getDepth());
            else pstmt.setNull(1, Types.DOUBLE);
            pstmt.setObject(2, cave.getNumberOfTreasures(), Types.INTEGER);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                throw new SQLException("error.database.cave_id");
            }
        }
    }

    public DatabaseAddingResult insertDragonRecord(Connection connection, DragonDTO dragon, long coordId, Long caveId, long userId) throws SQLException {
        String[] columnsToReturn = {"id", "creation_date"};
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_DRAGON, columnsToReturn)) {
            pstmt.setString(1, dragon.getName());
            pstmt.setLong(2, coordId);
            pstmt.setLong(3, dragon.getAge());
            pstmt.setString(4, dragon.getColor().toString());
            pstmt.setString(5, dragon.getType() != null ? dragon.getType().name() : null);
            pstmt.setString(6, dragon.getCharacter() != null ? dragon.getCharacter().name() : null);
            pstmt.setObject(7, caveId, Types.INTEGER);
            pstmt.setLong(8, userId);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new DatabaseAddingResult(rs.getLong(1), new java.util.Date(rs.getTimestamp(2).getTime()));
                }
                throw new SQLException("error.database.dragon_id");            }
        }
    }

    public Stack<Dragon> getDragons(Connection connection) throws SQLException {
        Stack<Dragon> dragons = new Stack<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_DRAGONS);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Long dragonId = rs.getLong("dragon_id");
                String dragonName = rs.getString("dragon_name");
                Long dragonAge = rs.getLong("age");
                Color dragonColor = Color.valueOf(rs.getString("color"));
                DragonType dragonType = rs.getString("dragon_type") == null ? null : DragonType.valueOf(rs.getString("dragon_type"));
                DragonCharacter dragonCharacter = rs.getString("dragon_character") == null ? null : DragonCharacter.valueOf(rs.getString("dragon_character"));
                java.util.Date creationDate = new java.util.Date(rs.getTimestamp("creation_date").getTime());

                Coordinates coord = new Coordinates(rs.getDouble("x"), rs.getLong("y"));

                long tempDepth = rs.getLong("depth");
                Long depth = rs.wasNull() ? null : tempDepth;
                Integer numberOfTreasures = rs.getObject("number_of_treasures", Integer.class);

                DragonCave dragonCave = (depth != null || numberOfTreasures != null) ? new DragonCave(depth, numberOfTreasures) : null;
                String ownerLogin = rs.getString("owner_login");

                dragons.add(new Dragon(dragonId, dragonName, coord, creationDate, dragonAge, dragonColor, dragonType, dragonCharacter, dragonCave, ownerLogin));
            }
        }
        return dragons;
    }

    public DragonLinks getDragonLinks(Connection connection, long dragonId) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_DRAGON_LINKS)) {
            pstmt.setLong(1, dragonId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long userId = rs.getLong("user_id");
                    long coordId = rs.getLong("coordinates");
                    long tempCaveId = rs.getLong("dragon_cave");
                    Long caveId = rs.wasNull() ? null : tempCaveId;
                    return new DragonLinks(userId, coordId, caveId);
                }
                return null;
            }
        }
    }

    public void updateCoordinates(Connection connection, long coordId, Coordinates coordinates) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_COORD)) {
            pstmt.setDouble(1, coordinates.getX());
            pstmt.setDouble(2, coordinates.getY());
            pstmt.setLong(3, coordId);
            pstmt.executeUpdate();
        }
    }

    public void updateCave(Connection connection, long caveId, DragonCave cave) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_CAVE)) {
            if (cave.getDepth() != null) pstmt.setDouble(1, cave.getDepth());
            else pstmt.setNull(1, Types.DOUBLE);
            pstmt.setObject(2, cave.getNumberOfTreasures(), Types.INTEGER);
            pstmt.setLong(3, caveId);
            pstmt.executeUpdate();
        }
    }

    public void updateDragonRecord(Connection connection, long dragonId, DragonDTO dragon, Long caveId) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_DRAGON)) {
            pstmt.setString(1, dragon.getName());
            pstmt.setLong(2, dragon.getAge());
            pstmt.setString(3, dragon.getColor().toString());
            pstmt.setString(4, dragon.getType() != null ? dragon.getType().name() : null);
            pstmt.setString(5, dragon.getCharacter() != null ? dragon.getCharacter().name() : null);
            pstmt.setObject(6, caveId, Types.INTEGER);
            pstmt.setLong(7, dragonId);
            pstmt.executeUpdate();
        }
    }

    public void deleteDragon(Connection connection, long dragonId) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_DRAGON)) {
            pstmt.setLong(1, dragonId);
            pstmt.executeUpdate();
        }
    }

    public void deleteCoordinate(Connection connection, long coordId) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_COORD)) {
            pstmt.setLong(1, coordId);
            pstmt.executeUpdate();
        }
    }

    public void deleteCave(Connection connection, long caveId) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_CAVE)) {
            pstmt.setLong(1, caveId);
            pstmt.executeUpdate();
        }
    }

    public void deleteDragonsByUserId(Connection connection, long userId) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_BY_USER)) {
            pstmt.setLong(1, userId);
            pstmt.executeUpdate();
        }
    }

    public void deleteDragonsByCharacter(Connection connection, DragonCharacter character, long userId) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_BY_CHAR)) {
            pstmt.setString(1, character.name());
            pstmt.setLong(2, userId);
            pstmt.executeUpdate();
        }
    }

    public void deleteOrphanCoordinates(Connection connection) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_ORPHAN_COORDS)) {
            pstmt.executeUpdate();
        }
    }

    public void deleteOrphanCaves(Connection connection) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_ORPHAN_CAVES)) {
            pstmt.executeUpdate();
        }
    }

    public int getCollectionSize(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM dragons")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        }
    }

    public Long findLastIdByUser(Connection conn, long userId) throws SQLException {
        String sql = "SELECT id FROM dragons WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
                return null;
            }
        }
    }
}