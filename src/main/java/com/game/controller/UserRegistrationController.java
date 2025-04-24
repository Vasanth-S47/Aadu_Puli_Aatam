package com.game.controller;

import com.game.exception.EmailAlreadyExistsException;
import com.game.exception.InvalidInputException;
import com.game.service.UserService;
import com.game.utils.MessageUtil;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/auth/signup")
public class UserRegistrationController extends HttpServlet {
    private UserService userService;
    private Gson gson;

    public void init() throws ServletException {
        super.init();
        userService = userService.getInstance();
        gson = new Gson();
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter responseWriter = response.getWriter();

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");


        try {
            boolean isRegistered = userService.registerUser(name, email, password);
            if(isRegistered)
            {
                response.setStatus(HttpServletResponse.SC_CREATED);
                responseWriter.print(gson.toJson(MessageUtil.get("message.signup.success")));
            }
        }catch (InvalidInputException | EmailAlreadyExistsException e)
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
