package com.game.dao;

import com.game.database.DatabaseConnection;
import com.game.model.Game;
import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class GameDAO {
    private static volatile GameDAO instance;
    private static final Gson gson = new Gson();

    private GameDAO() {
    }

    public static GameDAO getInstance() {
        if (instance == null) {
            synchronized (GameDAO.class) {
                if (instance == null) {
                    instance = new GameDAO();
                }
            }
        }
        return instance;
    }

    public List<Game> fetchCompletedGameHistoryByUserId(int userId) throws SQLException {
        List<Game> games = new ArrayList<>();
        String query = "SELECT * FROM game_sessions WHERE player_id = ? AND NOT JSON_UNQUOTE(JSON_EXTRACT(game_state, '$.currentStatus')) = 'IN_PROGRESS' ORDER BY updated_at DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Game game = new Game.Builder()
                        .gameId(resultSet.getString("game_id"))
                        .playerId(resultSet.getInt("player_id"))
                        .serializedGameState(resultSet.getString("game_state"))
                        .createdAt(resultSet.getTimestamp("created_at"))
                        .updatedAt(resultSet.getTimestamp("updated_at"))
                        .build();
                games.add(game);
            }
        }

        return games;
    }

    public Game fetchOngoingGameByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM game_sessions " +
                "WHERE player_id = ? AND " +
                "JSON_UNQUOTE(JSON_EXTRACT(game_state, '$.currentStatus')) = 'IN_PROGRESS' LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Game.Builder()
                            .gameId(resultSet.getString("game_id"))
                            .playerId(resultSet.getInt("player_id"))
                            .serializedGameState(resultSet.getString("game_state"))
                            .createdAt(resultSet.getTimestamp("created_at"))
                            .updatedAt(resultSet.getTimestamp("updated_at"))
                            .build();
                }
            }
        }

        return null;
    }

    public void persistGameObject(Game game) throws SQLException {
        String serializedState = gson.toJson(game.getGameState());

        String query = "INSERT INTO game_sessions (game_id, player_id, game_state, updated_at) " +
                "VALUES (?, ?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE " +
                "player_id = VALUES(player_id), " +
                "game_state = VALUES(game_state), " +
                "updated_at = NOW()";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, game.getGameId());
            statement.setInt(2, game.getPlayerId());
            statement.setString(3, serializedState);
            statement.executeUpdate();
        }
    }


    public Game retrieveGameById(String gameId) throws SQLException {
        String query = "SELECT * FROM game_sessions WHERE game_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Game.Builder()
                            .gameId(resultSet.getString("game_id"))
                            .playerId(resultSet.getInt("player_id"))
                            .serializedGameState(resultSet.getString("game_state"))
                            .createdAt(resultSet.getTimestamp("created_at"))
                            .updatedAt(resultSet.getTimestamp("updated_at"))
                            .build();
                }
            }
        }
        return null;
    }

    public String fetchOngoingGameIdByPlayerId(int playerId) throws SQLException {
        String query = "SELECT game_id FROM game_sessions WHERE player_id = ? AND JSON_UNQUOTE(JSON_EXTRACT(game_state, '$.currentStatus')) = 'IN_PROGRESS' LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("game_id");
                }
            }
        }

        return null;
    }

    public boolean updateGameStatusToAbortByUserId(Integer userId) {
        final String UPDATE_GAME_STATUS_SQL =
                "UPDATE game_sessions " +
                        "SET game_state = JSON_SET(game_state, '$.currentStatus', 'ABORTED'), updated_at = NOW() " +
                        "WHERE player_id = ? AND JSON_UNQUOTE(JSON_EXTRACT(game_state, '$.currentStatus')) = 'IN_PROGRESS' ";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateGameStatusStatement = connection.prepareStatement(UPDATE_GAME_STATUS_SQL)) {

            updateGameStatusStatement.setInt(1, userId);

            int affectedRows = updateGameStatusStatement.executeUpdate();

            return affectedRows > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public boolean updateGameResult(String gameId, String result) {
        String query = "UPDATE game_sessions " +
                "SET game_state = JSON_SET(game_state, '$.currentStatus', ?), updated_at = NOW() " +
                "WHERE game_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, result);
            statement.setString(2, gameId);

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Integer getUserIdByGameId(String gameId) {
        String query = "SELECT player_id FROM game_sessions WHERE game_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, gameId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("player_id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Boolean checkGamePresent(String gameId) throws SQLException
    {
        String query = "SELECT 1 FROM game_sessions WHERE game_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, gameId);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}