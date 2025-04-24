package com.game.model;

public class GameAnalytics {
    private String gameId;
    private double totalGameTime;
    private double totalUserTime;
    private double totalBotTime;
    private double avgUserTime;
    private double avgBotTime;
    private GameLog userQuickestMove;
    private GameLog userSlowestMove;
    private GameLog botQuickestMove;
    private GameLog botSlowestMove;
    private int userMoveCount;
    private int userJumpCount;
    private int botMoveCount;
    private int botJumpCount;

    private GameAnalytics(GameAnalyticsBuilder builder) {
        this.gameId = builder.gameId;
        this.totalGameTime = builder.totalGameTime;
        this.totalUserTime = builder.totalUserTime;
        this.totalBotTime = builder.totalBotTime;
        this.avgUserTime = builder.avgUserTime;
        this.avgBotTime = builder.avgBotTime;
        this.userQuickestMove = builder.userQuickestMove;
        this.userSlowestMove = builder.userSlowestMove;
        this.botQuickestMove = builder.botQuickestMove;
        this.botSlowestMove = builder.botSlowestMove;
        this.userMoveCount = builder.userMoveCount;
        this.userJumpCount = builder.userJumpCount;
        this.botMoveCount = builder.botMoveCount;
        this.botJumpCount = builder.botJumpCount;
    }

    public String getGameId() { return gameId; }
    public double getTotalGameTime() { return totalGameTime; }
    public double getTotalUserTime() { return totalUserTime; }
    public double getTotalBotTime() { return totalBotTime; }
    public double getAvgUserTime() { return avgUserTime; }
    public double getAvgBotTime() { return avgBotTime; }
    public GameLog getUserQuickestMove() { return userQuickestMove; }
    public GameLog getUserSlowestMove() { return userSlowestMove; }
    public GameLog getBotQuickestMove() { return botQuickestMove; }
    public GameLog getBotSlowestMove() { return botSlowestMove; }
    public int getUserMoveCount() { return userMoveCount; }
    public int getUserJumpCount() { return userJumpCount; }
    public int getBotMoveCount() { return botMoveCount; }
    public int getBotJumpCount() { return botJumpCount; }

    public static class GameAnalyticsBuilder {
        private String gameId;
        private double totalGameTime;
        private double totalUserTime;
        private double totalBotTime;
        private double avgUserTime;
        private double avgBotTime;
        private GameLog userQuickestMove;
        private GameLog userSlowestMove;
        private GameLog botQuickestMove;
        private GameLog botSlowestMove;
        private int userMoveCount;
        private int userJumpCount;
        private int botMoveCount;
        private int botJumpCount;

        public GameAnalyticsBuilder setGameId(String gameId) {
            this.gameId = gameId;
            return this;
        }

        public GameAnalyticsBuilder setTotalGameTime(double totalGameTime) {
            this.totalGameTime = totalGameTime;
            return this;
        }

        public GameAnalyticsBuilder setTotalUserTime(double totalUserTime) {
            this.totalUserTime = totalUserTime;
            return this;
        }

        public GameAnalyticsBuilder setTotalBotTime(double totalBotTime) {
            this.totalBotTime = totalBotTime;
            return this;
        }

        public GameAnalyticsBuilder setAvgUserTime(double avgUserTime) {
            this.avgUserTime = avgUserTime;
            return this;
        }

        public GameAnalyticsBuilder setAvgBotTime(double avgBotTime) {
            this.avgBotTime = avgBotTime;
            return this;
        }

        public GameAnalyticsBuilder setUserQuickestMove(GameLog userQuickestMove) {
            this.userQuickestMove = userQuickestMove;
            return this;
        }

        public GameAnalyticsBuilder setUserSlowestMove(GameLog userSlowestMove) {
            this.userSlowestMove = userSlowestMove;
            return this;
        }

        public GameAnalyticsBuilder setBotQuickestMove(GameLog botQuickestMove) {
            this.botQuickestMove = botQuickestMove;
            return this;
        }

        public GameAnalyticsBuilder setBotSlowestMove(GameLog botSlowestMove) {
            this.botSlowestMove = botSlowestMove;
            return this;
        }

        public GameAnalyticsBuilder setUserMoveCount(int userMoveCount) {
            this.userMoveCount = userMoveCount;
            return this;
        }

        public GameAnalyticsBuilder setUserJumpCount(int userJumpCount) {
            this.userJumpCount = userJumpCount;
            return this;
        }

        public GameAnalyticsBuilder setBotMoveCount(int botMoveCount) {
            this.botMoveCount = botMoveCount;
            return this;
        }

        public GameAnalyticsBuilder setBotJumpCount(int botJumpCount) {
            this.botJumpCount = botJumpCount;
            return this;
        }

        public GameAnalytics build() {
            return new GameAnalytics(this);
        }
    }
}