package com.game.service;

import com.game.bot.AaduPuliBotGatway;
import com.game.model.GameConstant;
import com.game.dao.GameDAO;
import com.game.engine.GameEngine;
import com.game.exception.GameNotFoundException;
import com.game.exception.IllegalMoveException;
import com.game.exception.InvalidInputException;
import com.game.model.Game;
import com.game.model.GameState;
import com.game.utils.MessageUtil;

import org.json.JSONObject;

import java.sql.SQLException;
import java.util.*;

public class GameMoveService {
    private static volatile GameMoveService instance;
    private GameMoveService() {}

    public static GameMoveService getInstance() {
        if (instance == null) {
            synchronized (GameMoveService.class) {
                if (instance == null) {
                    instance = new GameMoveService();
                }
            }
        }
        return instance;
    }

    private final GameDAO dao = GameDAO.getInstance();
    private final GameEngine engine = new GameEngine();
    private final GameConstant gameConstant=GameConstant.getInstance();
    public Game placeGoat(String gameId, Integer targetPosition)
            throws InvalidInputException, GameNotFoundException, SQLException {

        validateParameters(gameId,0,targetPosition);
        Game game = dao.retrieveGameById(gameId);
        if (game == null) throw new GameNotFoundException(MessageUtil.get("error.game.not_found"));

        boolean canPlace = engine.placeGoat(game, targetPosition);
        if (!canPlace) return null;

        dao.persistGameObject(game);
        return game;
    }


    public Game moveGoat(String gameId, Integer sourcePosition, Integer targetPosition)
            throws InvalidInputException, GameNotFoundException, IllegalMoveException, SQLException {

        validateParameters(gameId,sourcePosition,targetPosition);

        Game game = dao.retrieveGameById(gameId);
        if (game == null) throw new GameNotFoundException(MessageUtil.get("error.game.not_found"));

        boolean canMove = engine.moveGoat(game, sourcePosition, targetPosition);
        if (!canMove) return null;

        dao.persistGameObject(game);
        return game;
    }


    public Game moveTiger(String gameId, Integer sourcePosition, Integer targetPosition)
            throws InvalidInputException, GameNotFoundException, IllegalMoveException, SQLException {

        validateParameters(gameId,sourcePosition,targetPosition);

        Game game = dao.retrieveGameById(gameId);
        if (game == null) throw new GameNotFoundException(MessageUtil.get("error.game.not_found"));

        boolean canMove = engine.moveTiger(game, sourcePosition, targetPosition);
        if (!canMove) {
            return tigerJump(gameId, sourcePosition, targetPosition);
        }

        dao.persistGameObject(game);
        return game;
    }


    public Game tigerJump(String gameId, Integer sourcePosition, Integer targetPosition)
            throws InvalidInputException, GameNotFoundException, IllegalMoveException, SQLException {

        validateParameters(gameId,sourcePosition,targetPosition);

        Game game = dao.retrieveGameById(gameId);
        if (game == null) throw new GameNotFoundException(MessageUtil.get("error.game.not_found"));

        boolean canJump = engine.tigerJump(game, sourcePosition, targetPosition);
        if (!canJump) throw new IllegalMoveException(MessageUtil.get("error.move.illegal"));

        dao.persistGameObject(game);
        return game;
    }


    /*public JSONObject getBotResponse(String gameId) throws Exception {
        if (gameId == null || gameId.trim().isEmpty()) {
            throw new IllegalArgumentException(MessageUtil.get("error.gameId.required"));
        }

        Game game = dao.retrieveGameById(gameId);
        if (game == null) throw new GameNotFoundException(MessageUtil.get("error.game.notfound"));
        AaduPuliBotService botService = AaduPuliBotService.getInstance();
        JSONObject response= botService.getAIPromptResponse(game.getGameState());


        Map<String,Object> parsed=parseBotActionResponse(response);
        String action = (String) parsed.get("action");
        Integer sourcePosition=(Integer) parsed.get("source");
        Integer targetPosition=(Integer) parsed.get("target");

        if(isValidResponse(game,action,sourcePosition,targetPosition))
             return parsed;

        Map<String, Object> suggestMove=suggestNextMove(gameId);
        return suggestMove;

    }*/
    public Map<String, Object> getBotResponse(String gameId) throws Exception {
        if (gameId == null || gameId.trim().isEmpty()) {
            throw new IllegalArgumentException(MessageUtil.get("error.gameId.required"));
        }

        Game game = dao.retrieveGameById(gameId);
        if (game == null) throw new GameNotFoundException(MessageUtil.get("error.game.notfound"));
        AaduPuliBotGatway aaduPuliBotGatway = AaduPuliBotGatway.getInstance();
        JSONObject response= aaduPuliBotGatway.getAIPromptResponse(game.getGameState());


        Map<String,Object> parsed=parseBotActionResponse(response);
        String action = (String) parsed.get("action");
        Integer sourcePosition=(Integer) parsed.get("source");
        Integer targetPosition=(Integer) parsed.get("target");

        if(isValidResponse(game,action,sourcePosition,targetPosition))
            return parsed;

        Map<String, Object> suggestMove=suggestNextMove(gameId);
        return suggestMove;

    }
    public Boolean isValidResponse(Game game,String action,Integer sourcePosition,Integer targetPosition)
    {
        if(action.equals("place"))
        {
            return engine.isValidTarget(targetPosition,game.getBoard());
        }
        if(action.equals("move"))
        {
            return engine.isValidMove(sourcePosition,targetPosition,1,game.getBoard());
        }
        return false;
    }
    public Map<String, Object> parseBotActionResponse(JSONObject response) {
        try {

            JSONObject candidate = response.getJSONArray("candidates").getJSONObject(0);


            JSONObject content = candidate.getJSONObject("content");
            String role = content.getString("role");


            String rawText = content.getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");


            String cleanedText = rawText.replaceAll("```json|```", "").trim();


            JSONObject actionJson = new JSONObject(cleanedText);


            Map<String, Object> result = new HashMap<>();
            result.put("role", role);
            result.put("source", actionJson.isNull("source") ? null : actionJson.getInt("source"));
            result.put("target", actionJson.getInt("target"));
            result.put("action", actionJson.getString("action"));

            return result;

        } catch (Exception e) {
            System.err.println("Failed to parse bot action response: " + e.getMessage());
            return null;
        }
    }


    public Map<String, Object> suggestNextMove(String gameId)
            throws InvalidInputException, GameNotFoundException, SQLException {

        if (gameId == null || gameId.trim().isEmpty()) {
            throw new InvalidInputException(MessageUtil.get("error.gameId.required"));
        }

        Game game = dao.retrieveGameById(gameId);
        if (game == null) throw new GameNotFoundException(MessageUtil.get("error.game.notfound"));

        GameState state = game.getGameState();
        int[] board = state.getBoard();

        if (state.getTotalGoatsPlaced() < 15) {
            List<Integer> emptySpots = new ArrayList<>();
            for (int i = 1; i < board.length; i++) {
                if (board[i] == 0) {
                    emptySpots.add(i);
                }
            }

            if (!emptySpots.isEmpty()) {
                int randomIndex = emptySpots.get(new Random().nextInt(emptySpots.size()));
                Map<String, Object> response = new HashMap<>();
                response.put("action", "place");
                response.put("target", randomIndex);
                return response;
            }
        } else {
            for (int i = 1; i < board.length; i++) {
                if (board[i] == 2) {
                    List<Integer> possibleMoves = gameConstant.VALID_MOVES.get(i);
                    if (possibleMoves != null) {
                        for (int move : possibleMoves) {
                            if (engine.isValidMove(i, move, 2, board)) {
                                Map<String, Object> response = new HashMap<>();
                                response.put("action", "move");
                                response.put("source", i);
                                response.put("target", move);
                                return response;
                            }
                        }
                    }
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("action", "none");
        response.put("source", 0);
        response.put("target", 0);
        return response;
    }


    //Helper Functions :
    private void validateParameters(String gameId,Integer sourcePosition,Integer targetPosition) throws InvalidInputException
    {
        if(gameId==null || sourcePosition==null || targetPosition==null)
        {
            throw new InvalidInputException(MessageUtil.get("error.parameter.missing"));
        }
    }
}
