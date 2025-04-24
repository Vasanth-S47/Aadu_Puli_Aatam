package com.game.model;

public class GameLog {
    private int fromPosition;
    private int toPosition;
    private double moveTime;
    private String movedBy;
    private String action;

    private GameLog(GameLogBuilder builder) {
        this.fromPosition = builder.fromPosition;
        this.toPosition = builder.toPosition;
        this.moveTime = builder.moveTime;
        this.movedBy = builder.movedBy;
        this.action = builder.action;
    }

    public int getFromPosition() {
        return fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }

    public double getMoveTime() {
        return moveTime;
    }

    public String getMovedBy() {
        return movedBy;
    }

    public String getAction() {
        return action;
    }

    public static class GameLogBuilder {
        private int fromPosition;
        private int toPosition;
        private double moveTime;
        private String movedBy;
        private String action;

        public GameLogBuilder setFromPosition(int fromPosition) {
            this.fromPosition = fromPosition;
            return this;
        }

        public GameLogBuilder setToPosition(int toPosition) {
            this.toPosition = toPosition;
            return this;
        }

        public GameLogBuilder setMoveTime(double moveTime) {
            this.moveTime = moveTime;
            return this;
        }

        public GameLogBuilder setMovedBy(String movedBy) {
            this.movedBy = movedBy;
            return this;
        }

        public GameLogBuilder setAction(String action) {
            this.action = action;
            return this;
        }

        public GameLog build() {
            return new GameLog(this);
        }
    }
}
