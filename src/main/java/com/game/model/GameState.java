package com.game.model;

public class GameState {

    public static final int EMPTY = 0;
    public static final int TIGER = 1;
    public static final int GOAT = 2;

    public enum Status {
        IN_PROGRESS,
        WIN,
        LOSS,
        ABORTED
    }

    private int[] board;
    private String userRole;
    private Status currentStatus;
    private int totalGoatsPlaced;
    private int totalGoatsCaptured;
    private int totalTigersLocked;

    private GameState(Builder builder) {
        this.userRole = builder.userRole;
        this.board = initializeBoard();
        this.currentStatus = builder.status;
        this.totalGoatsPlaced = 0;
        this.totalGoatsCaptured = 0;
        this.totalTigersLocked = 0;
    }

    public static class Builder {
        private final String userRole;
        private Status status = Status.IN_PROGRESS;

        public Builder(String userRole) {
            this.userRole = userRole;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public GameState build() {
            return new GameState(this);
        }
    }

    private int[] initializeBoard() {
        int[] layout = new int[24];
        layout[1] = TIGER;
        layout[4] = TIGER;
        layout[5] = TIGER;
        return layout;
    }

    // Getters and Setters

    public int[] getBoard() {
        return board;
    }

    public void setBoard(int[] board) {
        this.board = board;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Status getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Status currentStatus) {
        this.currentStatus = currentStatus;
    }

    public int getTotalGoatsPlaced() {
        return totalGoatsPlaced;
    }

    public void setTotalGoatsPlaced(int totalGoatsPlaced) {
        this.totalGoatsPlaced = totalGoatsPlaced;
    }

    public int getTotalGoatsCaptured() {
        return totalGoatsCaptured;
    }

    public void setTotalGoatsCaptured(int totalGoatsCaptured) {
        this.totalGoatsCaptured = totalGoatsCaptured;
    }

    public int getTotalTigersLocked() {
        return totalTigersLocked;
    }

    public void setTotalTigersLocked(int totalTigersLocked) {
        this.totalTigersLocked = totalTigersLocked;
    }
}
