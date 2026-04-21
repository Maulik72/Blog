package com.blog.dao;

import com.blog.model.Post;
import com.blog.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public List<Post> getAllPublishedPosts() {
        List<Post> posts = new ArrayList<>();
        // We JOIN with the users table to get the author's username
        String sql = "SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id WHERE p.status = 'PUBLISHED' ORDER BY p.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setStatus(rs.getString("status"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                post.setAuthorName(rs.getString("username")); // From the JOIN

                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    // Method to insert a new post
    public boolean createPost(Post post) {
        // Notice we don't insert 'status' or 'created_at' because MySQL handles the defaults!
        String sql = "INSERT INTO posts (user_id, title, content) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getTitle());
            stmt.setString(3, post.getContent());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update this method signature in PostDAO.java to accept the loggedInUserId
    public Post getPostById(int id, int loggedInUserId) {
        // SQL with subqueries to get counts and user's specific reaction
        String sql = "SELECT p.*, u.username, " +
                "(SELECT COUNT(*) FROM post_reactions WHERE post_id = p.id AND reaction_type = 'LIKE') AS likes_count, " +
                "(SELECT COUNT(*) FROM post_reactions WHERE post_id = p.id AND reaction_type = 'DISLIKE') AS dislikes_count, " +
                "(SELECT reaction_type FROM post_reactions WHERE post_id = p.id AND user_id = ?) AS user_reaction " +
                "FROM posts p JOIN users u ON p.user_id = u.id WHERE p.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loggedInUserId);
            stmt.setInt(2, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setAuthorName(rs.getString("username"));
                post.setCreatedAt(rs.getTimestamp("created_at"));

                // Set the new reaction fields
                post.setLikesCount(rs.getInt("likes_count"));
                post.setDislikesCount(rs.getInt("dislikes_count"));
                post.setCurrentUserReaction(rs.getString("user_reaction"));

                return post;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deletePost(int postId, int userId) {
        boolean isDeleted = false;

        // Queries
        String deleteCommentsQuery = "DELETE FROM comments WHERE post_id = ?";
        String deletePostQuery = "DELETE FROM posts WHERE id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // Optional but recommended: Disable auto-commit to treat this as a single transaction
            conn.setAutoCommit(false);

            try {
                // 1. Delete associated comments first (to avoid Foreign Key constraint errors)
                try (PreparedStatement psComments = conn.prepareStatement(deleteCommentsQuery)) {
                    psComments.setInt(1, postId);
                    psComments.executeUpdate();
                }

                // 2. Delete the post (only if the user_id matches)
                try (PreparedStatement psPost = conn.prepareStatement(deletePostQuery)) {
                    psPost.setInt(1, postId);
                    psPost.setInt(2, userId);
                    int rowsAffected = psPost.executeUpdate();

                    if (rowsAffected > 0) {
                        isDeleted = true;
                        conn.commit(); // Commit transaction if post was successfully deleted
                    } else {
                        conn.rollback(); // Rollback if the post wasn't found or user didn't own it
                    }
                }
            } catch (SQLException e) {
                conn.rollback(); // Rollback on any error during the transaction
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true); // Restore default behavior
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isDeleted;
    }

    // Fetch all posts written by a specific user
    public List<Post> getPostsByUserId(int userId) {
        List<Post> posts = new ArrayList<>();
        // Re-use your existing logic but filter by user_id
        String sql = "SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id WHERE p.user_id = ? ORDER BY p.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content")); // Might want to truncate this in JSP
                post.setCreatedAt(rs.getTimestamp("created_at"));
                post.setAuthorName(rs.getString("username"));
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    // Add this to com.blog.dao.PostDAO
    public boolean updatePost(int postId, int userId, String title, String content) {
        String sql = "UPDATE posts SET title = ?, content = ? WHERE id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setInt(3, postId);
            stmt.setInt(4, userId); // Validates ownership!

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 1. Fetch a specific "page" of posts
    public List<Post> getPublishedPostsByPage(int offset, int limit) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id WHERE p.status = 'PUBLISHED' ORDER BY p.created_at DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setStatus(rs.getString("status"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                post.setAuthorName(rs.getString("username"));
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    // 2. Get the absolute total number of published posts
    public int getTotalPublishedPostsCount() {
        String sql = "SELECT COUNT(*) FROM posts WHERE status = 'PUBLISHED'";
        int total = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt(1); // Grabs the COUNT result
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

}