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

@WebServlet("/game/lobby")
public class GameLobbyController extends HttpServlet {

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
        PrintWriter responseWriter = response.getWriter();
        String userIdStr=request.getParameter("userId");
        try {
            Integer userId=Integer.parseInt(userIdStr);
            Game activeGame = gameLobbyService.getActiveGame(userId);
            request.setAttribute("activeGame", activeGame);

            request.getRequestDispatcher("/jsp/gameLobby1.jsp").forward(request, response);


        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseWriter.print(gson.toJson(MessageUtil.get("error.server.internal")));
        }
    }
}
