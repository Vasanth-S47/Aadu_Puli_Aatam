package com.game.controller;

import com.game.exception.GameAbortFailedException;
import com.game.exception.GameNotFoundException;
import com.game.exception.InvalidInputException;
import com.game.model.Game;
import com.game.service.GameService;
import com.game.utils.MessageUtil;
import com.google.gson.Gson;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
/**
 * REST‑style controller that exposes CRUD‑like endpoints for the {@code Game} resource.
 *
 * <pre>
 *   GET    /api/game?userId={userId}        → resume the user’s unfinished game
 *   POST   /api/game                        → create a new game  (role, userId in form body)
 *   DELETE /api/game/{userId}               → abort the user’s current game
 * </pre>
 *
 * All responses are returned as JSON; on error an HTTP status code plus a JSON
 * message is sent.
 *
 * <p>Thread‑safe: relies on the singleton {@link GameService} which is itself
 * thread‑safe.</p>
 */

@WebServlet("/api/game/*")
public class GameController extends HttpServlet {

    private Gson gson;
    private GameService gameService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        gson = new Gson();
        gameService = GameService.getInstance();
    }

    // GET- Resume game
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter responseWriter = response.getWriter();

        try {
            String userId = request.getParameter("userId");

            Game game = gameService.resumeGame(userId);
            response.setStatus(HttpServletResponse.SC_OK);
            responseWriter.print(gson.toJson(game));

        } catch (InvalidInputException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseWriter.print(gson.toJson(e.getMessage()));
        } catch (GameNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseWriter.print(gson.toJson(e.getMessage()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseWriter.print(gson.toJson("Server error: " + e.getMessage()));
            e.printStackTrace();
        } finally {
            responseWriter.flush();
        }
    }

    // POST - Create new game
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter responseWriter = response.getWriter();

        try {
            String role = request.getParameter("role");
            String userIdStr =request.getParameter("userId");
            Integer userId=Integer.parseInt(userIdStr);
            Game game = gameService.createNewGame(role, userId);
            responseWriter.print(gson.toJson(game));
        } catch (InvalidInputException e) {
            responseWriter.print(gson.toJson(e.getMessage()));
        } catch (Exception e) {
            responseWriter.print(gson.toJson("Server error: " + e.getMessage()));
            e.printStackTrace();
        } finally {
            responseWriter.flush();
        }
    }
    //DELETE - Abort the game
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter responseWriter = response.getWriter();
        try {
            String userIdStr= request.getPathInfo().substring(1);
            boolean isGameAborted = gameService.abortGame(userIdStr);
            if (isGameAborted) {
                responseWriter.print(gson.toJson(MessageUtil.get("message.game.abort.success")));
            }
        } catch (NumberFormatException | NullPointerException | InvalidInputException | GameAbortFailedException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseWriter.print(gson.toJson(e.getMessage()));
        } catch (Exception e) {
            responseWriter.print(gson.toJson(MessageUtil.get("error.server.internal")));
            e.printStackTrace();
        } finally {
            responseWriter.flush();
        }

    }

}
