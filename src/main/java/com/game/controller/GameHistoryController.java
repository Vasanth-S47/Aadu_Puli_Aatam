package com.game.controller;

import com.game.model.Game;
import com.game.model.User;
import com.game.service.GameLobbyService;
import com.game.utils.CookieUtil;
import com.game.utils.MessageUtil;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/game/history")
public class GameHistoryController extends HttpServlet {

    private  GameLobbyService gameLobbyService;
    private  Gson gson ;


    @Override
    public void init() throws ServletException {
        super.init();
        gameLobbyService = GameLobbyService.getInstance();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter responseWriter = response.getWriter();
        String userIdStr=request.getParameter("userId");
        try {
            Integer userId=Integer.parseInt(userIdStr);
            List<Game> gameHistory = gameLobbyService.getCompletedGames(userId);
            response.setStatus(HttpServletResponse.SC_OK);
            responseWriter.print(gson.toJson(gameHistory));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseWriter.print(gson.toJson(MessageUtil.get("error.server.internal")));
        }
    }
}
