package com.game.model;

import com.google.gson.Gson;

import java.sql.Timestamp;

public class Game {

    private final String gameId;
    private final int playerId;
    private final GameState gameState;
    private final Timestamp createdAt;
    private final Timestamp updatedAt;

    private static final Gson gson = new Gson();

    private Game(Builder builder) {
        this.gameId = builder.gameId;
        this.playerId = builder.playerId;
        this.gameState = builder.gameState;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public static class Builder {
        private String gameId;
        private int playerId;
        private GameState gameState;
        private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());

        public Builder gameId(String gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder playerId(int playerId) {
            this.playerId = playerId;
            return this;
        }

        public Builder gameState(GameState gameState) {
            this.gameState = gameState;
            return this;
        }

        public Builder serializedGameState(String jsonState) {
            this.gameState = gson.fromJson(jsonState, GameState.class);
            return this;
        }

        public Builder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Timestamp updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Game build() {
            return new Game(this);
        }
    }

    public String getGameId() {
        return gameId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public GameState getGameState() {
        return gameState;
    }
    public int[] getBoard()
    {
        return gameState.getBoard();
    }
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public String getUserRole() {
        return gameState.getUserRole();
    }

    public String getStatus() {
        return gameState.getCurrentStatus().name();
    }

    public int getGoatsPlaced() {
        return gameState.getTotalGoatsPlaced();
    }

    public int getGoatsEaten() {
        return gameState.getTotalGoatsCaptured();
    }

    public int getTigersLocked() {
        return gameState.getTotalTigersLocked();
    }
}
