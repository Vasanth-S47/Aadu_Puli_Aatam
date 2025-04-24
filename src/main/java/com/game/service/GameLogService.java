package com.game.service;

import com.game.dao.GameDAO;
import com.game.dao.GameLogDAO;
import com.game.exception.GameLogNotFoundException;
import com.game.exception.GameNotFoundException;
import com.game.model.GameLog;
import com.game.model.GameAnalytics;
import com.game.utils.MessageUtil;


import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class GameLogService {

    private static volatile GameLogService instance;

    private final GameLogDAO gameLogDAO = GameLogDAO.getInstance();
    private final GameDAO gameDAO=GameDAO.getInstance();
    private GameLogService() {}

    public static GameLogService getInstance() {
        if (instance == null) {
            synchronized (GameLogService.class) {
                if (instance == null) {
                    instance = new GameLogService();
                }
            }
        }
        return instance;
    }

    public boolean handleGameMoveLog(String gameId, String fromPositionStr, String toPositionStr,
                                     String moveTimeStr, String movedBy, String action)
            throws IllegalArgumentException, SQLException ,GameNotFoundException{


        if (gameId == null || toPositionStr == null || moveTimeStr == null || movedBy == null || action == null) {
            throw new IllegalArgumentException(MessageUtil.get("error.missing.parameter"));
        }

        int fromPosition = parseOrDefault(fromPositionStr, 0);
        int toPosition = parseInteger(toPositionStr);
        double moveTime = parseDouble(moveTimeStr);
        boolean isGamePresent = gameDAO.checkGamePresent(gameId);
        if(!isGamePresent)
        {
            throw new GameNotFoundException(MessageUtil.get("error.game.not_found"));
        }
        GameLog gameLog = new GameLog.GameLogBuilder()
                .setFromPosition(fromPosition)
                .setToPosition(toPosition)
                .setMoveTime(moveTime)
                .setMovedBy(movedBy)
                .setAction(action)
                .build();

        return gameLogDAO.addGameLog(gameLog, gameId);
    }


    public GameAnalytics analyzeGame(String gameId) {
        if (gameId == null || gameId.trim().isEmpty()) {
            throw new IllegalArgumentException(MessageUtil.get("error.gameId.required"));
        }
        List<GameLog> gameLogs = gameLogDAO.getGameLogsByGameId(gameId);
        if (gameLogs.isEmpty()) {
            throw new GameLogNotFoundException(MessageUtil.get("error.gamelog.notfound"));
        }

        return buildGameAnalytics(gameId, gameLogs);
    }




    private GameAnalytics buildGameAnalytics(String gameId, List<GameLog> logs) {
        double totalGameTime = calculateTotalTime(logs);

        List<GameLog> userLogs = filterByMover(logs, "USER");
        List<GameLog> botLogs = filterByMover(logs, "BOT");

        return new GameAnalytics.GameAnalyticsBuilder()
                .setGameId(gameId)
                .setTotalGameTime(totalGameTime)
                .setTotalUserTime(calculateTotalTime(userLogs))
                .setTotalBotTime(calculateTotalTime(botLogs))
                .setAvgUserTime(calculateAverageTime(userLogs))
                .setAvgBotTime(calculateAverageTime(botLogs))
                .setUserQuickestMove(findQuickestMove(userLogs))
                .setUserSlowestMove(findSlowestMove(userLogs))
                .setBotQuickestMove(findQuickestMove(botLogs))
                .setBotSlowestMove(findSlowestMove(botLogs))
                .setUserMoveCount(countActions(userLogs, "MOVE"))
                .setUserJumpCount(countActions(userLogs, "JUMP"))
                .setBotMoveCount(countActions(botLogs, "MOVE"))
                .setBotJumpCount(countActions(botLogs, "JUMP"))
                .build();
    }

    private int parseOrDefault(String value, int defaultVal) {
        try {
            return value != null ? Integer.parseInt(value) : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(MessageUtil.get("eerror.input.format.invalid"));
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(MessageUtil.get("error.input.format.invalid"));
        }
    }

    private List<GameLog> filterByMover(List<GameLog> logs, String mover) {
        return logs.stream().filter(log -> mover.equals(log.getMovedBy())).collect(Collectors.toList());
    }

    private double calculateTotalTime(List<GameLog> logs) {
        return logs.stream().mapToDouble(GameLog::getMoveTime).sum();
    }

    private double calculateAverageTime(List<GameLog> logs) {
        return logs.isEmpty() ? 0 : logs.stream().mapToDouble(GameLog::getMoveTime).average().orElse(0);
    }

    private GameLog findQuickestMove(List<GameLog> logs) {
        return logs.stream()
                .filter(log -> "MOVE".equals(log.getAction()))
                .min(Comparator.comparingDouble(GameLog::getMoveTime))
                .orElse(null);
    }

    private GameLog findSlowestMove(List<GameLog> logs) {
        return logs.stream()
                .filter(log -> "MOVE".equals(log.getAction()))
                .max(Comparator.comparingDouble(GameLog::getMoveTime))
                .orElse(null);
    }

    private int countActions(List<GameLog> logs, String actionType) {
        return (int) logs.stream()
                .filter(log -> actionType.equals(log.getAction()))
                .count();
    }
}
