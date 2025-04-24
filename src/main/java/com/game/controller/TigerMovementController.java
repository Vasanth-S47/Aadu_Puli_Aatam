package com.game.controller;

import com.game.model.Game;

import com.game.service.GameMoveService;
import com.game.utils.MessageUtil;
import com.google.gson.Gson;
import com.game.exception.*;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/game/movement-tiger")
public class TigerMovementController extends HttpServlet {
    private  Gson gson = new Gson();
    private GameMoveService gamePlayService;

    public void init() throws ServletException {
        super.init();
        gamePlayService = GameMoveService.getInstance();
        gson = new Gson();
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter responseWriter = response.getWriter();
        String data = (String) request.getAttribute("cachedBody");

        //JSONObject jsonObject = JsonRequestBodyReader.getJsonRequestBody(request);
        try {
            JSONObject jsonObject = new JSONObject(data);
            String gameId =jsonObject.getString("gameId");
            Integer sourcePosition= jsonObject.getInt("source");
            Integer targetPosition = jsonObject.getInt("target");

            Game game = gamePlayService.moveTiger(gameId, sourcePosition, targetPosition);
            responseWriter.print(gson.toJson(game.getGameState()));

        } catch (InvalidInputException | GameNotFoundException | IllegalMoveException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            responseWriter.print(gson.toJson(e.getMessage()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseWriter.print(gson.toJson(MessageUtil.get("error.server.internal")));

        } finally {
            responseWriter.flush();
        }
    }
}
