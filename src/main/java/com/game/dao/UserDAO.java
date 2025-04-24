package com.game.dao;

import com.game.database.DatabaseConnection;
import com.game.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private static volatile UserDAO instance;

    private UserDAO() {}

    public static UserDAO getInstance() {
        if (instance == null) {
            synchronized (UserDAO.class) {
                if (instance == null) {
                    instance = new UserDAO();
                }
            }
        }
        return instance;
    }

    public String fetchUsernameByUserId(int userId) throws SQLException {
        String query = "SELECT name FROM users WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        }
        return null;
    }

    public boolean checkEmailExists(String email) throws SQLException {
        String query = "SELECT id FROM users WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public boolean registerNewUser(User user) throws SQLException {
        String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashedPassword);
            return statement.executeUpdate() > 0;
        }
    }

    public User fetchUserByEmail(String email) throws SQLException {
        String query = "SELECT name, email, password FROM users WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new User.UserBuilder()
                        .setName(resultSet.getString("name"))
                        .setEmail(resultSet.getString("email"))
                        .setPassword(resultSet.getString("password"))
                        .build();
            }
        }
        return null;
    }

    public boolean updateTokenByEmail(String email, String token) throws SQLException {
        String query = "UPDATE users SET token = ? WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, token);
            statement.setString(2, email);
            return statement.executeUpdate() > 0;
        }
    }

    public User fetchUserByToken(String token) throws SQLException {
        String query = "SELECT name, email, password FROM users WHERE token = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new User.UserBuilder()
                        .setName(resultSet.getString("name"))
                        .setEmail(resultSet.getString("email"))
                        .setPassword(resultSet.getString("password"))
                        .build();
            }
        }
        return null;
    }

    public Integer fetchUserIdByEmail(String email) throws SQLException {
        String query = "SELECT id FROM users WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        }
        return null;
    }

    public Integer fetchUserIdByToken(String token) throws SQLException {
        String query = "SELECT id FROM users WHERE token = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        }
        return null;
    }

    public void removeCookieTokenByUserId(Integer userId) throws SQLException
    {
        String query = "UPDATE users  SET token =NULL WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }

    }
}
