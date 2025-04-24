package com.game.controller;

import com.game.exception.InvalidInputException;
import com.game.service.UserService;
import com.game.utils.MessageUtil;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/auth/logout")

public class UserLogoutController extends HttpServlet {
    private UserService userService;
    private Gson gson ;

    public void init() throws ServletException {
        super.init();
        userService = UserService.getInstance();
        gson = new Gson();
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter responseWriter = response.getWriter();
        String userIdStr=request.getParameter("userId");

        try {
            Integer userId = Integer.parseInt(userIdStr);
            userService.logout(userId);
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("user_token".equals(cookie.getName())) {
                        cookie.setValue("");
                        cookie.setPath("/Aadu_Puli_Aatam");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }

            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            response.setStatus(HttpServletResponse.SC_OK);
        }catch (NumberFormatException | InvalidInputException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseWriter.print(gson.toJson(MessageUtil.get("error.input.format.invalid")));
        }catch (Exception e)
        {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            responseWriter.print(gson.toJson(MessageUtil.get("error.server.internal")));
        }

    }
}


