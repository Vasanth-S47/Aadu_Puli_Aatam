package com.game.controller;

import com.game.exception.GameNotFoundException;
import com.game.service.GameMoveService;
import com.game.utils.MessageUtil;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/api/bot/*")
public class BotController extends HttpServlet {
    private  Gson gson;
    private GameMoveService gameMoveService;

    public void init() throws ServletException {
        super.init();
        gameMoveService = GameMoveService.getInstance();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter responseWriter = response.getWriter();

        String gameId = request.getParameter("gameId");
        try {
            Map<String,Object> jsonResponse = gameMoveService.getBotResponse(gameId);
            responseWriter.print(gson.toJson(jsonResponse));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseWriter.print(gson.toJson(e.getMessage()));
        } catch (GameNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseWriter.print(gson.toJson(e.getMessage()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            responseWriter.print(gson.toJson(MessageUtil.get("error.server.internal")));
        }
        finally {
            responseWriter.flush();
        }
    }
}
