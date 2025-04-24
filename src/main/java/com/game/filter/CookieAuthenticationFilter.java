package com.game.filter;

import com.game.dao.UserDAO;
import com.game.model.User;
import com.game.utils.CookieUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;


public class CookieAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String[] unprotectedPaths = {"/login", "/auth/login","/signup","/auth/signup","/css", "/images", "/favicon.ico"};

        String path = request.getRequestURI().substring(request.getContextPath().length());

        for (String unprotected : unprotectedPaths) {
            if (path.startsWith(unprotected)) {
                chain.doFilter(req, res);
                return;
            }
        }
        String token = CookieUtil.getCookieValue(request, "user_token");
        if (token != null) {
            try {
                UserDAO userDAO = UserDAO.getInstance();
                User user = userDAO.fetchUserByToken(token);

                if (user != null) {
                    chain.doFilter(req, res);
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        response.sendRedirect(request.getContextPath() + "/login");


    }
}
