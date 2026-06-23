package ru.itmo.nemat.services;

import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.database.UserDAO;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.ClientManager;
import ru.itmo.nemat.models.*;
import ru.itmo.nemat.utils.DragonDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class DragonService {

    private final DragonDAO dragonDAO;
    private final UserDAO userDAO;
    private final ClientManager clientManager;

    private final java.util.Date lastInitTime = new java.util.Date();
    private static final Logger logger = Logger.getLogger(DragonService.class.getName());


    public String getInfoMessage(Connection conn) throws SQLException {
        int size = dragonDAO.getCollectionSize(conn);
        return "Stack," + size + "," + lastInitTime;

    }

    public DragonService(DragonDAO dragonDAO, UserDAO userDAO, ClientManager clientManager) {
        this.dragonDAO = dragonDAO;
        this.userDAO = userDAO;
        this.clientManager = clientManager;
    }

    private void broadcastEvent(ResponseStatus actionType, List<Dragon> affectedDragons) {
        Response updateResponse = new Response("UPDATE_UI", actionType, affectedDragons);
        clientManager.broadcast(updateResponse);
    }

    public ResponseStatus addDragon(Connection conn, String login, DragonDTO dragon) {
        try {
            long userId = userDAO.getUserIdByLogin(conn, login);
            conn.setAutoCommit(false);
            try {
                long coordId = dragonDAO.insertCoordinates(conn, dragon.getCoordinates());
                Long caveId = dragon.getCave() != null ? dragonDAO.insertCave(conn, dragon.getCave()) : null;
                long newDragonId = dragonDAO.insertDragonRecord(conn, dragon, coordId, caveId, userId).id();

                Dragon fullNewDragon = dragonDAO.getDragonById(conn, newDragonId);

                conn.commit();
                broadcastEvent(ResponseStatus.DRAGON_ADDED, List.of(fullNewDragon));
                return ResponseStatus.OK;
            } catch (SQLException e) {
                conn.rollback();
                return ResponseStatus.ERROR;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return ResponseStatus.ERROR;
        }
    }

    public ResponseStatus deleteDragonById(Connection conn, String login, long dragonId) {
        try {
            long userId = userDAO.getUserIdByLogin(conn, login);
            DragonDAO.DragonLinks links = dragonDAO.getDragonLinks(conn, dragonId);

            if (links == null) return ResponseStatus.NOT_FOUND;
            if (links.userId != userId) return ResponseStatus.PERMISSION_DENIED;

            Dragon dragonToDelete = dragonDAO.getDragonById(conn, dragonId);

            conn.setAutoCommit(false);
            try {
                logger.info("Попытка удалить дракона с айди " + dragonId);
                dragonDAO.deleteDragon(conn, dragonId);
                logger.info("Дракон удален");
                logger.info("Попытка удалить координату с айди " + links.coordId);
                dragonDAO.deleteCoordinate(conn, links.coordId);

                if (links.caveId != null) dragonDAO.deleteCave(conn, links.caveId);

                conn.commit();
                broadcastEvent(ResponseStatus.DRAGON_REMOVED, List.of(dragonToDelete));
                return ResponseStatus.OK;
            } catch (SQLException e) {
                conn.rollback();
                logger.warning(e.getMessage());
                return ResponseStatus.ERROR;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            return ResponseStatus.ERROR;
        }
    }

    public ResponseStatus updateDragon(Connection conn, String login, long dragonId, DragonDTO dragon) {
        try {
            long userId = userDAO.getUserIdByLogin(conn, login);
            DragonDAO.DragonLinks links = dragonDAO.getDragonLinks(conn, dragonId);

            if (links == null) return ResponseStatus.NOT_FOUND;
            if (links.userId != userId) return ResponseStatus.PERMISSION_DENIED;

            conn.setAutoCommit(false);
            try {
                dragonDAO.updateCoordinates(conn, links.coordId, dragon.getCoordinates());

                Long finalCaveId = links.caveId;
                Long caveToDelete = null;

                if (links.caveId == null && dragon.getCave() != null) {
                    finalCaveId = dragonDAO.insertCave(conn, dragon.getCave());
                } else if (links.caveId != null && dragon.getCave() == null) {
                    finalCaveId = null;
                    caveToDelete = links.caveId;
                } else if (links.caveId != null && dragon.getCave() != null) {
                    dragonDAO.updateCave(conn, links.caveId, dragon.getCave());
                }

                dragonDAO.updateDragonRecord(conn, dragonId, dragon, finalCaveId);
                if (caveToDelete != null) dragonDAO.deleteCave(conn, caveToDelete);

                Dragon updatedDragon = dragonDAO.getDragonById(conn, dragonId);

                conn.commit();
                broadcastEvent(ResponseStatus.DRAGON_UPDATED, List.of(updatedDragon));
                return ResponseStatus.OK;
            } catch (SQLException e) {
                conn.rollback();
                return ResponseStatus.ERROR;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return ResponseStatus.ERROR;
        }
    }

    public ResponseStatus clearUserCollection(Connection conn, String login) {
        try {
            long userId = userDAO.getUserIdByLogin(conn, login);

            List<Dragon> dragonsToDelete = dragonDAO.getDragonsByUserId(conn, userId);

            conn.setAutoCommit(false);
            try {
                dragonDAO.deleteDragonsByUserId(conn, userId);
                dragonDAO.deleteOrphanCoordinates(conn);
                dragonDAO.deleteOrphanCaves(conn);

                conn.commit();
                if (!dragonsToDelete.isEmpty()) {
                    broadcastEvent(ResponseStatus.DRAGON_REMOVED, dragonsToDelete);
                }
                return ResponseStatus.OK;
            } catch (SQLException e) {
                conn.rollback();
                return ResponseStatus.ERROR;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return ResponseStatus.ERROR;
        }
    }

    public ResponseStatus deleteDragonsByCharacter(Connection conn, String login, DragonCharacter character) {
        try {
            long userId = userDAO.getUserIdByLogin(conn, login);

            List<Dragon> toDelete = dragonDAO.getDragonsByCharacterAndUser(conn, character, userId);
            if (toDelete.isEmpty()) return ResponseStatus.OK;

            conn.setAutoCommit(false);
            try {
                dragonDAO.deleteDragonsByCharacter(conn, character, userId);
                dragonDAO.deleteOrphanCoordinates(conn);
                dragonDAO.deleteOrphanCaves(conn);

                conn.commit();
                broadcastEvent(ResponseStatus.DRAGON_REMOVED, toDelete);
                return ResponseStatus.OK;
            } catch (SQLException e) {
                conn.rollback();
                return ResponseStatus.ERROR;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return ResponseStatus.ERROR;
        }
    }

    public List<Dragon> getAllDragons(Connection conn) throws SQLException {
        return dragonDAO.getDragons(conn);
    }

    public ResponseStatus removeLast(Connection conn, String login) {
        try {
            long userId = userDAO.getUserIdByLogin(conn, login);
            Long lastId = dragonDAO.findLastIdByUser(conn, userId);

            if (lastId == null) return ResponseStatus.NOT_FOUND;

            return deleteDragonById(conn, login, lastId);
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            return ResponseStatus.ERROR;

        }
    }

    public void syncAllClients(Connection conn) throws SQLException {
        List<Dragon> allDragons = dragonDAO.getDragons(conn);
        java.util.Collections.sort(allDragons);
        broadcastEvent(ResponseStatus.UPDATE_NOTIFICATION, allDragons);
    }
}