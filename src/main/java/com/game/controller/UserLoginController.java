package com.game.servlet;

import com.game.dao.UserDAO;
import com.game.model.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class UserLoginController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();
        try {
            User user = userDAO.getUserByEmail(email);

            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                String token = BCrypt.hashpw(String.valueOf(user.getId()), BCrypt.gensalt(12));

                Cookie loginCookie = new Cookie("user_token", token);
                loginCookie.setHttpOnly(true);
                loginCookie.setMaxAge(7 * 24 * 60 * 60);
                loginCookie.setPath("/");
                response.addCookie(loginCookie);

                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // wrong credentials
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
