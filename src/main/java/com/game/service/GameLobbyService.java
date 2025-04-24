package com.game.service;

import com.game.dao.GameDAO;
import com.game.model.Game;
import java.util.Collections;
import java.util.List;

public class GameLobbyService {
    private static volatile GameLobbyService instance;
    private GameLobbyService() {
    }
    public static GameLobbyService getInstance() {
        if (instance == null) {
            synchronized (GameLobbyService.class) {
                if (instance == null) {
                    instance = new GameLobbyService();
                }
            }
        }
        return instance;
    }
    private final GameDAO gameDAO = GameDAO.getInstance();

    public List<Game> getCompletedGames(int userId) {
        if (userId <= 0) {
            return Collections.emptyList();
        }
        try {
            return gameDAO.fetchCompletedGameHistoryByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Game getActiveGame(int userId) {
        if (userId <= 0) {
            return null;
        }

        try {
            return gameDAO.fetchOngoingGameByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
