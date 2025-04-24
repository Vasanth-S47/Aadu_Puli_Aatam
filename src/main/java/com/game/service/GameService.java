package com.game.service;

import com.game.dao.GameDAO;
import com.game.exception.GameAbortFailedException;
import com.game.exception.GameNotFoundException;
import com.game.exception.InvalidInputException;
import com.game.model.Game;
import com.game.model.GameState;
import com.game.utils.MessageUtil;

import java.sql.SQLException;
import java.util.UUID;

public class GameService {

    private static volatile GameService instance;
    private GameService() { }

    public static GameService getInstance() {
        if (instance == null) {
            synchronized (GameService.class) {
                if (instance == null) {
                    instance = new GameService();
                }
            }
        }
        return instance;
    }

    private final GameDAO gameDao = GameDAO.getInstance();


    public boolean abortGame(String userIdStr) throws InvalidInputException, GameAbortFailedException {
        if (userIdStr == null) {
            throw new InvalidInputException(MessageUtil.get("error.user.id.required"));
        }
        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(MessageUtil.get("error.input.format.invalid"));
        }

        boolean isGameAborted = gameDao.updateGameStatusToAbortByUserId(userId);
        if (!isGameAborted) {
            throw new GameAbortFailedException(MessageUtil.get("error.game.abort.failed"));
        }
        return true;
    }


    public Game createNewGame(String role, Integer userId) throws InvalidInputException, SQLException {
        if (role == null || userId == null) {
            throw new InvalidInputException(MessageUtil.get("error.parameter.missing"));
        }

        String gameId = UUID.randomUUID().toString();
        GameState gameState = new GameState.Builder(role).build();
        Game game = new Game.Builder()
                .gameId(gameId)
                .playerId(userId)
                .gameState(gameState)
                .build();

        gameDao.persistGameObject(game);
        return game;
    }


    public Game resumeGame(String userIdStr) throws InvalidInputException, GameNotFoundException, SQLException {
        if (userIdStr == null) {
            throw new InvalidInputException(MessageUtil.get("error.parameter.missing"));
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(MessageUtil.get("error.user.id.invalid"));
        }

        String gameId = gameDao.fetchOngoingGameIdByPlayerId(userId);
        if (gameId == null) {
            throw new GameNotFoundException(MessageUtil.get("error.game.id.not_found"));
        }

        Game game = gameDao.retrieveGameById(gameId);
        if (game == null) {
            throw new GameNotFoundException(MessageUtil.get("error.game.not_found"));
        }

        return game;
    }

    public boolean updateGameResult(String gameId, String result) {
        if (gameId == null || gameId.isEmpty()) {
            throw new IllegalArgumentException(MessageUtil.get("error.game.id.required"));
        }
        if (result == null || result.isEmpty()) {
            throw new IllegalArgumentException("error.result.required");
        }
        if (!"WIN".equalsIgnoreCase(result) && !"LOSS".equalsIgnoreCase(result)) {
            throw new IllegalArgumentException("Invalid result type");
        }

        return gameDao.updateGameResult(gameId, result);
    }


}
