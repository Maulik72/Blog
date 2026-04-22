package com.blog.dao;

import com.blog.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AdminDAO {

    // Fetches the total counts for the admin dashboard overview
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();

        try (Connection conn = DBConnection.getConnection()) {
            stats.put("totalUsers", getCount(conn, "SELECT COUNT(*) FROM users"));
            stats.put("totalPosts", getCount(conn, "SELECT COUNT(*) FROM posts"));
            stats.put("totalCategories", getCount(conn, "SELECT COUNT(*) FROM categories"));
            stats.put("totalComments", getCount(conn, "SELECT COUNT(*) FROM comments"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // Helper method to execute a simple COUNT query
    private int getCount(Connection conn, String sql) {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}