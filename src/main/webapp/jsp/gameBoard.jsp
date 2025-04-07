<%@ page import="javax.servlet.ServletContext" %>
<%
    String BASE_URL = application.getInitParameter("BASE_URL");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aadu Puli Aatam - Game Board</title>
    <link rel="stylesheet" href="<%= BASE_URL %>src/css/gameBoard.css">
</head>
<body>
    <div class="header">
        <h1>Aadu Puli Aatam</h1>
        <div class="game-info">
            <div id="playerInfo">Player: <span id="playerName">...</span></div>
            <div id="gameStatus">Place your goats!</div>
            <div id="gameTiming">Time: <span id="gameTimer">00:00</span></div>
        </div>
        <div class="controls">
            <button id="pauseGame">Pause</button>
            <button id="restartGame">Restart</button>
            <button id="quitGame">Quit</button>
        </div>
    </div>

    <div class="container">
        <div class="game-board">
            <img src="board.png" id="board">
        </div>

        <div class="game-sidebar">
            <div class="score-board">
                <div class="player-score">
                    <div class="goat-count">Goats: <span id="goatsRemaining">15</span> | <span id="goatsCaptured">0</span> captured</div>
                </div>
                <div class="bot-score">
                    <div class="tiger-count">Tigers: 3</div>
                </div>
            </div>

            <div class="move-history">
                <h3>Move History</h3>
                <div id="moveHistoryContainer"></div>
            </div>
        </div>
    </div>

    <!-- Game state modals -->
    <div id="gameOverModal" class="modal hidden">
        <div class="modal-content">
            <h2 id="gameResultTitle">Game Over</h2>
            <p id="gameResultMessage"></p>
            <div class="modal-buttons">
                <button id="playAgainBtn">Play Again</button>
                <button id="returnToMenuBtn">Return to Menu</button>
            </div>
        </div>
    </div>

    <div id="pauseModal" class="modal hidden">
        <div class="modal-content">
            <h2>Game Paused</h2>
            <div class="modal-buttons">
                <button id="resumeGameBtn">Resume Game</button>
            </div>
        </div>
    </div>
   <script type="module" defer src="<%= BASE_URL %>src/js/findPoints.js"></script>
</body>
</html>