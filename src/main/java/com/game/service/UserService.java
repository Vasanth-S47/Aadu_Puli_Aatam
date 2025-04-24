package com.game.service;

import com.game.dao.UserDAO;
import com.game.exception.*;
import com.game.model.User;
import com.game.utils.MessageUtil;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private static volatile UserService instance;

    private UserService() {
    }

    public static UserService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }

    private final UserDAO userDAO = UserDAO.getInstance();


    public Integer login(String email, String password) throws Exception {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidInputException(MessageUtil.get("error.input.invalid"));
        }

        User user = userDAO.fetchUserByEmail(email);
        int userId = userDAO.fetchUserIdByEmail(email);

        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return userId;
        } else {
            throw new InvalidCredentialsException(MessageUtil.get("error.invalid.credentials"));
        }
    }


    public String getUsername(String userIdStr) throws Exception {
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            throw new InvalidInputException(MessageUtil.get("error.invalid.input"));
        }

        Integer userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(MessageUtil.get("error.input.format.invalid"));
        }

        String username = userDAO.fetchUsernameByUserId(userId);
        if (username != null) {
            return username;
        } else {
            throw new UserNotFoundException(MessageUtil.get("error.user.not.found"));
        }
    }


    public boolean registerUser(String name, String email, String password) throws Exception {
        if (name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            throw new InvalidInputException(MessageUtil.get("error.input.format.invalid"));
        }

        if (userDAO.checkEmailExists(email)) {
            throw new EmailAlreadyExistsException(MessageUtil.get("error.email.already.exists"));
        }

        User user = new User.UserBuilder()
                .setName(name)
                .setEmail(email)
                .setPassword(password)
                .build();

        boolean inserted = userDAO.registerNewUser(user);
        if (!inserted) {
            throw new InvalidInputException(MessageUtil.get("error.input.format.invalid"));
        }

        return inserted;
    }

    public void logout(Integer userId) throws Exception
    {
     if(userId==null)
     {
         throw new InvalidInputException(MessageUtil.get("error.missing.parameter"));
     }
     userDAO.removeCookieTokenByUserId(userId);
    }
}
