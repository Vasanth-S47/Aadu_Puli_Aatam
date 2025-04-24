package com.game.controller;

import com.game.service.GameService;
import com.game.utils.JsonRequestBodyReader;
import com.game.utils.MessageUtil;
import com.google.gson.Gson;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/game/result/*")
public class GameResultController extends HttpServlet {
    private GameService gameService;
    private  Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        super.init();
        gameService= GameService.getInstance();
        gson = new Gson();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter responseWriter = response.getWriter();
        String data = (String) request.getAttribute("cachedBody");
        //JSONObject jsonObject = JsonRequestBodyReader.getJsonRequestBody(request);
        try {
            JSONObject jsonObject = new JSONObject(data);
            String gameId = jsonObject.getString("gameId");
            String result = jsonObject.getString("result");
            boolean success = gameService.updateGameResult(gameId, result);
            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                responseWriter.print(gson.toJson(MessageUtil.get("message.game.result.success")));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseWriter.print(gson.toJson(MessageUtil.get("message.game.result.failure")));
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseWriter.print(gson.toJson(e.getMessage()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseWriter.print(gson.toJson(MessageUtil.get("error.server.internal")));
        } finally {
            responseWriter.flush();
        }
    }
}
