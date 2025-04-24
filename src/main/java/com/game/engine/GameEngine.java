package com.game.engine;

import com.game.model.GameConstant;
import com.game.model.Game;
import com.game.model.GameState;

import java.util.List;
import java.util.Map;

public class GameEngine {

    private static final int BOARD_SIZE = 24;
    private static final int EMPTY = 0;
    private static final int TIGER = 1;
    private static final int GOAT = 2;

    private final GameConstant gameConstant=GameConstant.getInstance();

    public Boolean placeGoat(final Game game, final int targetPosition) {
        GameState state = game.getGameState();
        int[] board = state.getBoard();

        if (!isValidTarget(targetPosition, board)) {
            return false;
        }

        board[targetPosition] = GOAT;
        state.setTotalGoatsPlaced(state.getTotalGoatsPlaced() + 1);
        int tigersLocked=countTigersLocked(game);
        state.setTotalTigersLocked(tigersLocked);
        return true;
    }

    public Boolean moveGoat(final Game game, final int from, final int to) {
        GameState state = game.getGameState();
        int[] board = state.getBoard();

        if (!isValidMove(from, to, GOAT, board)) {
            return false;
        }

        board[from] = EMPTY;
        board[to] = GOAT;

        return true;
    }

    public Boolean moveTiger(final Game game, final int from, final int to) {
        GameState state = game.getGameState();
        int[] board = state.getBoard();

        if (!isValidMove(from, to, TIGER, board)) {
            return false;
        }

        board[from] = EMPTY;
        board[to] = TIGER;

        return true;
    }

    public Boolean tigerJump(final Game game, final int from, final int to) {
        GameState state = game.getGameState();
        int[] board = state.getBoard();

        if (!isValidJump(from, to, board)) {
            return false;
        }

        int goatToEat = gameConstant.VALID_JUMPS.get(from).get(to);
        board[goatToEat] = EMPTY;
        board[from] = EMPTY;
        board[to] = TIGER;

        state.setTotalGoatsCaptured(state.getTotalGoatsCaptured() + 1);

        return true;
    }

    public int countTigersLocked(final Game game) {
        GameState state = game.getGameState();
        int[] board = state.getBoard();

        int lockedTigers = 0;

        for (int pos = 0; pos < BOARD_SIZE; pos++) {
            if (board[pos] == TIGER) {
                boolean isLocked = true;

                List<Integer> moves = gameConstant.VALID_MOVES.get(pos);
                if (moves != null) {
                    for (int move : moves) {
                        if (board[move] == EMPTY) {
                            isLocked = false;
                            break;
                        }
                    }
                }

                Map<Integer, Integer> jumps = gameConstant.VALID_JUMPS.get(pos);
                if (jumps != null) {
                    for (Map.Entry<Integer, Integer> entry : jumps.entrySet()) {
                        int jumpTo = entry.getKey();
                        int goatBetween = entry.getValue();

                        if (board[jumpTo] == EMPTY && board[goatBetween] == GOAT) {
                            isLocked = false;
                            break;
                        }
                    }
                }

                if (isLocked) {
                    lockedTigers++;
                }
            }
        }

        state.setTotalTigersLocked(lockedTigers);
        return lockedTigers;
    }

    public boolean isValidTarget(int target, int[] board) {
        return target >= 0 && target < BOARD_SIZE && board[target] == EMPTY;
    }

    public boolean isValidMove(int from, int to, int pieceType, int[] board) {
        return from >= 0 && from < BOARD_SIZE
                && to >= 0 && to < BOARD_SIZE
                && board[from] == pieceType
                && board[to] == EMPTY
                && gameConstant.VALID_MOVES.containsKey(from)
                && gameConstant.VALID_MOVES.get(from).contains(to);
    }

    public boolean isValidJump(int from, int to, int[] board) {
        if (!gameConstant.VALID_JUMPS.containsKey(from)) return false;
        Map<Integer, Integer> jumps = gameConstant.VALID_JUMPS.get(from);
        if (!jumps.containsKey(to)) return false;

        int goatBetween = jumps.get(to);
        return board[from] == TIGER
                && board[to] == EMPTY
                && board[goatBetween] == GOAT;


    }
}
