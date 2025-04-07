package com.game.dao;

import com.game.database.DatabaseConnection;
import com.game.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean isEmailRegistered(String email) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id FROM users WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    public boolean insertUser(User user) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
            String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, hashedPassword);
                return stmt.executeUpdate() > 0;
            }
        }
    }

    public User getUserByEmail(String email) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, name, email, password FROM users WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setPassword(rs.getString("password")); // hashed password
                        return user;
                    }
                }
            }
        }
        return null;
    }
}
