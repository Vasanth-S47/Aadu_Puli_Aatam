package com.game.dao;

import com.game.database.DatabaseConnection;
import com.game.model.GameLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GameLogDAO {
    private static final Logger logger =Logger.getLogger(GameLogDAO.class.getName());

    private static GameLogDAO instance;

    private GameLogDAO() {

    }

    public static synchronized GameLogDAO getInstance() {
        if (instance == null) {
            instance = new GameLogDAO();
        }
        return instance;
    }

    public boolean addGameLog(GameLog gameLog, String gameId) {
        String query = "INSERT INTO game_logs (game_id, from_position, to_position, move_time, moved_by, action) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, gameId);
            stmt.setInt(2, gameLog.getFromPosition());
            stmt.setInt(3, gameLog.getToPosition());
            stmt.setDouble(4, gameLog.getMoveTime());
            stmt.setString(5, gameLog.getMovedBy());
            stmt.setString(6, gameLog.getAction());

            int rowsAffected = stmt.executeUpdate();


            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void logAllGameLogs() {
        String query = "SELECT * FROM game_logs";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Loop through all rows in the result set
            while (rs.next()) {
                // Retrieve each column value for the current row
                String gameId = rs.getString("game_id");
                int fromPosition = rs.getInt("from_position");
                int toPosition = rs.getInt("to_position");
                double moveTime = rs.getDouble("move_time");
                String movedBy = rs.getString("moved_by");
                String action = rs.getString("action");

                // Log the game log details
                logger.info("Game ID: " + gameId
                        + ", From Position: " + fromPosition
                        + ", To Position: " + toPosition
                        + ", Move Time: " + moveTime
                        + ", Moved By: " + movedBy
                        + ", Action: " + action);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<GameLog> getGameLogsByGameId(String gameId) {
        List<GameLog> logs = new ArrayList<>();
        String sql = "SELECT id, game_id, from_position, to_position, move_time, moved_by, action " +
                "FROM game_logs WHERE game_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gameId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                GameLog log = new GameLog.GameLogBuilder()
                        .setFromPosition(rs.getInt("from_position"))
                        .setToPosition(rs.getInt("to_position"))
                        .setMoveTime(rs.getDouble("move_time"))
                        .setMovedBy(rs.getString("moved_by"))
                        .setAction(rs.getString("action"))
                        .build();
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    /*public List<GameLog> getGameLogsByGameId(String gameId) {
        List<GameLog> gameLogs = new ArrayList<>();
        String query = "SELECT * FROM game_logs WHERE game_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, gameId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                GameLog gameLog = new GameLog(
                        rs.getInt("from_position"),
                        rs.getInt("to_position"),
                        rs.getDouble("move_time"),
                        rs.getString("moved_by"),
                        rs.getString("action")
                );
                gameLogs.add(gameLog);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gameLogs;
    }*/

}
