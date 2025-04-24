package com.game.controller;

import com.game.exception.InvalidInputException;
import com.game.exception.UserNotFoundException;
import com.game.service.UserService;
import com.game.utils.MessageUtil;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/username")
public class UsernameController extends HttpServlet {
    private  Gson gson;
    private UserService userService;

    public void init() throws ServletException {
        super.init();
        userService = UserService.getInstance();
        gson = new Gson();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter responseWriter = response.getWriter();

        String userIdStr = request.getParameter("userId");
        try {
            String username=userService.getUsername(userIdStr);
            response.setStatus(HttpServletResponse.SC_OK);
            responseWriter.print(gson.toJson(username));
        }catch (InvalidInputException | NumberFormatException | UserNotFoundException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseWriter.print(gson.toJson(e.getMessage()));
        }
        catch (Exception e)
        {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseWriter.print(gson.toJson(MessageUtil.get("error.server.internal")));
        }
    }
}
