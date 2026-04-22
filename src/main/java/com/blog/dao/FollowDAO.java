package com.blog.dao;

import com.blog.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.blog.model.User;
import java.util.ArrayList;
import java.util.List;

public class FollowDAO {

    // 1. Check if User A is following User B
    public boolean isFollowing(int followerId, int followingId) {
        String sql = "SELECT * FROM followers WHERE follower_id = ? AND following_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            stmt.setInt(2, followingId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if the record exists
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // 2. Toggle the follow relationship (Follow/Unfollow)
    public void toggleFollow(int followerId, int followingId) {
        if (followerId == followingId) return; // You can't follow yourself!

        if (isFollowing(followerId, followingId)) {
            // Unfollow
            String sql = "DELETE FROM followers WHERE follower_id = ? AND following_id = ?";
            executeUpdate(sql, followerId, followingId);
        } else {
            // Follow
            String sql = "INSERT INTO followers (follower_id, following_id) VALUES (?, ?)";
            executeUpdate(sql, followerId, followingId);
        }
    }

    // 3. Count how many people follow this user
    public int getFollowerCount(int userId) {
        String sql = "SELECT COUNT(*) FROM followers WHERE following_id = ?";
        return getCount(sql, userId);
    }

    // 4. Count how many people this user is following
    public int getFollowingCount(int userId) {
        String sql = "SELECT COUNT(*) FROM followers WHERE follower_id = ?";
        return getCount(sql, userId);
    }

    // Helper method for clean code
    private void executeUpdate(String sql, int id1, int id2) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id1);
            stmt.setInt(2, id2);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Helper method for clean code
    private int getCount(String sql, int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Fetch the list of users who are following this user
    public List<User> getFollowers(int userId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.profile_image_url, u.bio FROM users u JOIN followers f ON u.id = f.follower_id WHERE f.following_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setProfileImageUrl(rs.getString("profile_image_url"));
                    user.setBio(rs.getString("bio"));
                    users.add(user);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    // Fetch the list of users that this user is currently following
    public List<User> getFollowing(int userId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.profile_image_url, u.bio FROM users u JOIN followers f ON u.id = f.following_id WHERE f.follower_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setProfileImageUrl(rs.getString("profile_image_url"));
                    user.setBio(rs.getString("bio"));
                    users.add(user);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

}