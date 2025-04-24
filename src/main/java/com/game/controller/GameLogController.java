package com.game.controller;

import com.game.exception.GameNotFoundException;
import com.game.service.GameLogService;
import com.game.utils.MessageUtil;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/game/move-logs")
public class GameLogController extends HttpServlet {
    private  GameLogService gameLogService;
    private  Gson gson = new Gson();
    @Override
    public void init() throws ServletException {
        super.init();
        gameLogService = GameLogService.getInstance();
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter responseWriter = response.getWriter();

        try {
            String gameId = request.getParameter("gameId");
            String fromPosition = request.getParameter("fromPosition");
            String toPosition = request.getParameter("toPosition");
            String moveTime = request.getParameter("moveTime");
            String movedBy = request.getParameter("movedBy");
            String action = request.getParameter("action");

            boolean success = gameLogService.handleGameMoveLog(gameId, fromPosition, toPosition, moveTime, movedBy, action);

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                responseWriter.print(gson.toJson(MessageUtil.get("message.log.success")));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseWriter.print(gson.toJson(MessageUtil.get("message.log.failure")));
            }
        } catch (GameNotFoundException | IllegalArgumentException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseWriter.print(gson.toJson(e.getMessage()));
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseWriter.print(gson.toJson(MessageUtil.get("error.server.internal")));
        }
    }
}
