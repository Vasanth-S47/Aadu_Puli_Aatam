package com.game.controller;

import com.game.dao.UserDAO;
import com.game.exception.InvalidCredentialsException;
import com.game.exception.InvalidInputException;
import com.game.service.UserService;
import com.game.utils.MessageUtil;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/auth/login")
public class UserLoginController extends HttpServlet {
    private UserService userService;
    private  Gson gson ;

    public void init() throws ServletException {
        super.init();
        userService = UserService.getInstance();
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        PrintWriter responseWriter = response.getWriter();

       try {
            Integer userId = userService.login(email, password);

            if(userId != null){

                Map<String, Integer> userIdMap = new HashMap<>();
                userIdMap.put("userId",userId);
                String loginToken= generateToken(userId);
                Cookie loginCookie = new Cookie("user_token", loginToken);
                loginCookie.setHttpOnly(true);
                loginCookie.setMaxAge(7 * 24 * 60 * 60);
                loginCookie.setPath("/Aadu_Puli_Aatam");
                response.addCookie(loginCookie);
                UserDAO.getInstance().updateTokenByEmail(email,loginToken);

                response.setStatus(HttpServletResponse.SC_OK);
                responseWriter.print(gson.toJson(userIdMap));
            }
        } catch (InvalidInputException | InvalidCredentialsException e)
       {
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           responseWriter.print(gson.toJson(e.getMessage()));
       }
       catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseWriter.print(gson.toJson(MessageUtil.get("error.server.internal")));

        }
    }
    private String generateToken(int userId) {
        return BCrypt.hashpw(String.valueOf(userId), BCrypt.gensalt(12));
    }
}
