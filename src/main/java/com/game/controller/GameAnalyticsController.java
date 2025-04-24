package com.game.controller;

import com.game.exception.GameLogNotFoundException;
import com.game.model.GameAnalytics;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.game.service.GameLogService;
import com.game.utils.MessageUtil;
import com.google.gson.Gson;

@WebServlet("/api/analytics/game/*")
public class GameAnalyticsController extends HttpServlet {

    private GameLogService gameLogService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        gameLogService = GameLogService.getInstance();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        String gameId = request.getPathInfo().substring(1);
        PrintWriter responseWriter = response.getWriter();

        try {
            GameAnalytics analytics = gameLogService.analyzeGame(gameId);
                response.setStatus(HttpServletResponse.SC_OK);
                responseWriter.print(gson.toJson(analytics));

        }
        catch(IllegalArgumentException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseWriter.print(gson.toJson(e.getMessage()));
        }
        catch (GameLogNotFoundException e)
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseWriter.print(gson.toJson(e.getMessage()));
        }
        catch(Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseWriter.print(gson.toJson(MessageUtil.get("error.server.internal")));
            e.printStackTrace();
        }
    }
}