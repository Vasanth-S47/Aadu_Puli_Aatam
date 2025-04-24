package com.game.filter;

import com.game.dao.GameDAO;
import com.game.dao.UserDAO;
import com.game.utils.CookieUtil;
import com.game.utils.JsonRequestBodyReader;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class UserAuthorizationFilter implements Filter {

    private UserDAO userDao;
    private GameDAO gameDao;

    @Override
    public void init(FilterConfig filterConfig) {
        userDao =UserDAO.getInstance();
        gameDao =GameDAO.getInstance();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {


        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setContentType("application/json");
        boolean isAuthorized = true;
        String httpMethod = request.getMethod();
        try {
            Integer requestedUserId = null;

            if ("GET".equals(httpMethod) || "POST".equals(httpMethod)) {
                String userIdStr = request.getParameter("userId");
                String gameId = request.getParameter("gameId");
                if (userIdStr != null) {
                    requestedUserId = Integer.parseInt(userIdStr);
                } else if (gameId != null) {
                    requestedUserId = gameDao.getUserIdByGameId(gameId);
                }
            } else if("PUT".equals(httpMethod) || "PATCH".equals(httpMethod) ){

                JSONObject jsonObject = JsonRequestBodyReader.getJsonRequestBody(request);
                request.setAttribute("cachedBody",jsonObject.toString());
                if (jsonObject.has("userId")) {
                    requestedUserId = jsonObject.getInt("userId");
                } else if (jsonObject.has("gameId")) {
                    String gameId = jsonObject.getString("gameId");
                    requestedUserId = gameDao.getUserIdByGameId(gameId);
                }
            } else if("DELETE".equals(httpMethod))
            {
                String userIdStr= request.getPathInfo().substring(1);
                if(userIdStr!=null)
                {
                    requestedUserId=Integer.parseInt(userIdStr);
                }
            }
            if(requestedUserId!=null) {
                isAuthorized = checkAuthorization(request, requestedUserId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            isAuthorized = false;
        }

        if (isAuthorized) {
            chain.doFilter(req, res);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    private boolean checkAuthorization(HttpServletRequest request, Integer requestedUserId) throws Exception{
        String token = CookieUtil.getCookieValue(request, "user_token");
        if (token == null) return false;

        Integer tokenUserId = userDao.fetchUserIdByToken(token);
        return tokenUserId != null && tokenUserId.equals(requestedUserId);
    }

}
