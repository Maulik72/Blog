package com.blog.dao;

import com.blog.model.User;
import com.blog.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class UserDAO {

    // 1. Method to Register a new user
    public boolean registerUser(User user) {
        // Hash the password before saving!
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, hashedPassword);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Returns true if insert was successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Method to Login a user
    public User loginUser(String email, String plainPassword) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");

                // Check if the typed password matches the hashed password in the DB
                if (BCrypt.checkpw(plainPassword, storedHashedPassword)) {
                    // Passwords match! Create a user object to return
                    User loggedInUser = new User();
                    loggedInUser.setId(rs.getInt("id"));
                    loggedInUser.setUsername(rs.getString("username"));
                    loggedInUser.setEmail(rs.getString("email"));
                    loggedInUser.setRole(rs.getString("role"));
                    loggedInUser.setBio(rs.getString("bio"));
                    loggedInUser.setProfileImageUrl(rs.getString("profile_image_url"));

                    return loggedInUser;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Login failed (wrong email or password)
    }

    // Fetch a user by their ID
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setBio(rs.getString("bio"));
                user.setProfileImageUrl(rs.getString("profile_image_url"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update bio and profile image URL
    public boolean updateProfile(int userId, String bio, String profileImageUrl) {
        String sql = "UPDATE users SET bio = ?, profile_image_url = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bio);
            stmt.setString(2, profileImageUrl);
            stmt.setInt(3, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Search users by their username
    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, bio, profile_image_url, role FROM users WHERE username LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setBio(rs.getString("bio"));
                user.setProfileImageUrl(rs.getString("profile_image_url"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    // --- ADMIN METHODS ---

    // 1. Fetch all users for the admin panel
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setProfileImageUrl(rs.getString("profile_image_url"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                users.add(user);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    // 2. Change a user's role (Promote to Admin or Demote to User)
    public boolean updateUserRole(int userId, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newRole);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Delete a user completely
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}