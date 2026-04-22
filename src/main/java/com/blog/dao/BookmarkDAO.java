package com.blog.dao;

import com.blog.model.Post;
import com.blog.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookmarkDAO {

    // Toggles the bookmark on or off
    public void toggleBookmark(int userId, int postId) {
        String checkSql = "SELECT * FROM bookmarks WHERE user_id = ? AND post_id = ?";
        String insertSql = "INSERT INTO bookmarks (user_id, post_id) VALUES (?, ?)";
        String deleteSql = "DELETE FROM bookmarks WHERE user_id = ? AND post_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, postId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // It exists -> Remove it
                try (PreparedStatement delStmt = conn.prepareStatement(deleteSql)) {
                    delStmt.setInt(1, userId);
                    delStmt.setInt(2, postId);
                    delStmt.executeUpdate();
                }
            } else {
                // Doesn't exist -> Save it
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, postId);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Fetches all posts a specific user has bookmarked
    public List<Post> getSavedPostsByUserId(int userId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, c.name AS category_name " +
                "FROM posts p " +
                "JOIN bookmarks b ON p.id = b.post_id " +
                "JOIN users u ON p.user_id = u.id " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "WHERE b.user_id = ? ORDER BY b.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                int catId = rs.getInt("category_id");
                if (!rs.wasNull()) {
                    post.setCategoryId(catId);
                    post.setCategoryName(rs.getString("category_name"));
                }
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setAuthorName(rs.getString("username"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                posts.add(post);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return posts;
    }
}