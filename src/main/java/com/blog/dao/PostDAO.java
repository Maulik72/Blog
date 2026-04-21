package com.blog.dao;

import com.blog.model.Post;
import com.blog.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // <-- Added this for generated keys!
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public List<Post> getAllPublishedPosts() {
        List<Post> posts = new ArrayList<>();
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
                post.setAuthorName(rs.getString("username"));
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    // Legacy method - kept for backward compatibility if needed
    public boolean createPost(Post post) {
        String sql = "INSERT INTO posts (user_id, title, content) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getTitle());
            stmt.setString(3, post.getContent());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Post getPostById(int id, int loggedInUserId) {
        String sql = "SELECT p.*, u.username, c.name AS category_name, " +
                "(SELECT COUNT(*) FROM post_reactions WHERE post_id = p.id AND reaction_type = 'LIKE') AS likes_count, " +
                "(SELECT COUNT(*) FROM post_reactions WHERE post_id = p.id AND reaction_type = 'DISLIKE') AS dislikes_count, " +
                "(SELECT reaction_type FROM post_reactions WHERE post_id = p.id AND user_id = ?) AS user_reaction " +
                "FROM posts p " +
                "JOIN users u ON p.user_id = u.id " +
                "LEFT JOIN categories c ON p.category_id = c.id " + // <--- NEW JOIN
                "WHERE p.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loggedInUserId);
            stmt.setInt(2, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));

                // --- NEW: Category Data ---
                int catId = rs.getInt("category_id");
                if (!rs.wasNull()) {
                    post.setCategoryId(catId);
                    post.setCategoryName(rs.getString("category_name"));
                }
                // --------------------------

                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setAuthorName(rs.getString("username"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                post.setLikesCount(rs.getInt("likes_count"));
                post.setDislikesCount(rs.getInt("dislikes_count"));
                post.setCurrentUserReaction(rs.getString("user_reaction"));
                return post;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean deletePost(int postId, int userId) {
        boolean isDeleted = false;
        String deleteCommentsQuery = "DELETE FROM comments WHERE post_id = ?";
        String deletePostQuery = "DELETE FROM posts WHERE id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psComments = conn.prepareStatement(deleteCommentsQuery)) {
                    psComments.setInt(1, postId);
                    psComments.executeUpdate();
                }
                try (PreparedStatement psPost = conn.prepareStatement(deletePostQuery)) {
                    psPost.setInt(1, postId);
                    psPost.setInt(2, userId);
                    if (psPost.executeUpdate() > 0) {
                        isDeleted = true;
                        conn.commit();
                    } else {
                        conn.rollback();
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    public List<Post> getPostsByUserId(int userId) {
        List<Post> posts = new ArrayList<>();
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
                post.setContent(rs.getString("content"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                post.setAuthorName(rs.getString("username"));
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public boolean updatePost(int postId, int userId, String title, String content) {
        String sql = "UPDATE posts SET title = ?, content = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setInt(3, postId);
            stmt.setInt(4, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Post> getPublishedPostsByPage(int offset, int limit) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, c.name AS category_name " +
                "FROM posts p JOIN users u ON p.user_id = u.id " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "WHERE p.status = 'PUBLISHED' ORDER BY p.created_at DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));

                // Fetch Category Data
                int catId = rs.getInt("category_id");
                if (!rs.wasNull()) {
                    post.setCategoryId(catId);
                    post.setCategoryName(rs.getString("category_name"));
                }

                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setStatus(rs.getString("status"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                post.setAuthorName(rs.getString("username"));
                posts.add(post);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return posts;
    }

    // 2. NEW: Fetch published posts filtered by a specific Category ID
    public List<Post> getPublishedPostsByCategoryAndPage(int categoryId, int offset, int limit) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username, c.name AS category_name " +
                "FROM posts p JOIN users u ON p.user_id = u.id " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "WHERE p.status = 'PUBLISHED' AND p.category_id = ? " +
                "ORDER BY p.created_at DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setCategoryId(rs.getInt("category_id"));
                post.setCategoryName(rs.getString("category_name"));
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setStatus(rs.getString("status"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                post.setAuthorName(rs.getString("username"));
                posts.add(post);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return posts;
    }

    // 3. NEW: Get the total number of published posts for a specific Category (For Pagination)
    public int getTotalPublishedPostsCountByCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM posts WHERE status = 'PUBLISHED' AND category_id = ?";
        int total = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) total = rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return total;
    }

    public int getTotalPublishedPostsCount() {
        String sql = "SELECT COUNT(*) FROM posts WHERE status = 'PUBLISHED'";
        int total = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // --- NEW: Advanced Method for Categories and Tags ---
    public boolean createPostWithTags(Post post, String tagsInput) {
        String insertPostSql = "INSERT INTO posts (user_id, category_id, title, content) VALUES (?, ?, ?, ?)";
        String checkTagSql = "SELECT id FROM tags WHERE name = ?";
        String insertTagSql = "INSERT INTO tags (name) VALUES (?)";
        String linkTagSql = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction

            try {
                int generatedPostId = -1;

                // 1. Insert the Post
                try (PreparedStatement psPost = conn.prepareStatement(insertPostSql, Statement.RETURN_GENERATED_KEYS)) {
                    psPost.setInt(1, post.getUserId());

                    if (post.getCategoryId() != null && post.getCategoryId() > 0) {
                        psPost.setInt(2, post.getCategoryId());
                    } else {
                        psPost.setNull(2, java.sql.Types.INTEGER); // Handle uncategorized
                    }

                    psPost.setString(3, post.getTitle());
                    psPost.setString(4, post.getContent());
                    psPost.executeUpdate();

                    try (ResultSet rsKeys = psPost.getGeneratedKeys()) {
                        if (rsKeys.next()) {
                            generatedPostId = rsKeys.getInt(1);
                        }
                    }
                }

                // 2. Process Tags
                if (generatedPostId != -1 && tagsInput != null && !tagsInput.trim().isEmpty()) {
                    String[] tags = tagsInput.split(",");

                    for (String rawTag : tags) {
                        String tagName = rawTag.trim().toLowerCase();
                        if (tagName.isEmpty()) continue;

                        int tagId = -1;

                        try (PreparedStatement psCheck = conn.prepareStatement(checkTagSql)) {
                            psCheck.setString(1, tagName);
                            try (ResultSet rsCheck = psCheck.executeQuery()) {
                                if (rsCheck.next()) tagId = rsCheck.getInt("id");
                            }
                        }

                        if (tagId == -1) {
                            try (PreparedStatement psInsertTag = conn.prepareStatement(insertTagSql, Statement.RETURN_GENERATED_KEYS)) {
                                psInsertTag.setString(1, tagName);
                                psInsertTag.executeUpdate();
                                try (ResultSet rsTagKeys = psInsertTag.getGeneratedKeys()) {
                                    if (rsTagKeys.next()) tagId = rsTagKeys.getInt(1);
                                }
                            }
                        }

                        if (tagId != -1) {
                            try (PreparedStatement psLink = conn.prepareStatement(linkTagSql)) {
                                psLink.setInt(1, generatedPostId);
                                psLink.setInt(2, tagId);
                                psLink.executeUpdate();
                            }
                        }
                    }
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. NEW METHOD: Fetch tags for a specific post
    public String getTagsForPost(int postId) {
        StringBuilder tags = new StringBuilder();
        String sql = "SELECT t.name FROM tags t JOIN post_tags pt ON t.id = pt.tag_id WHERE pt.post_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (tags.length() > 0) tags.append(", ");
                tags.append(rs.getString("name"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tags.toString();
    }

    // 3. NEW METHOD: Update a post and overwrite its tags
    public boolean updatePostWithTags(Post post, String tagsInput) {
        String updatePostSql = "UPDATE posts SET title = ?, content = ?, category_id = ? WHERE id = ? AND user_id = ?";
        String deleteOldTagsSql = "DELETE FROM post_tags WHERE post_id = ?";
        String checkTagSql = "SELECT id FROM tags WHERE name = ?";
        String insertTagSql = "INSERT INTO tags (name) VALUES (?)";
        String linkTagSql = "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction
            try {
                // A. Update the Posts table
                try (PreparedStatement psPost = conn.prepareStatement(updatePostSql)) {
                    psPost.setString(1, post.getTitle());
                    psPost.setString(2, post.getContent());
                    if (post.getCategoryId() != null && post.getCategoryId() > 0) {
                        psPost.setInt(3, post.getCategoryId());
                    } else {
                        psPost.setNull(3, java.sql.Types.INTEGER);
                    }
                    psPost.setInt(4, post.getId());
                    psPost.setInt(5, post.getUserId());

                    if (psPost.executeUpdate() == 0) {
                        conn.rollback(); return false; // Security check failed
                    }
                }

                // B. Wipe old tags for this post
                try (PreparedStatement psDelete = conn.prepareStatement(deleteOldTagsSql)) {
                    psDelete.setInt(1, post.getId());
                    psDelete.executeUpdate();
                }

                // C. Insert/Link new tags (Same logic as Create)
                if (tagsInput != null && !tagsInput.trim().isEmpty()) {
                    String[] tags = tagsInput.split(",");
                    for (String rawTag : tags) {
                        String tagName = rawTag.trim().toLowerCase();
                        if (tagName.isEmpty()) continue;
                        int tagId = -1;

                        try (PreparedStatement psCheck = conn.prepareStatement(checkTagSql)) {
                            psCheck.setString(1, tagName);
                            ResultSet rsCheck = psCheck.executeQuery();
                            if (rsCheck.next()) tagId = rsCheck.getInt("id");
                        }
                        if (tagId == -1) {
                            try (PreparedStatement psInsertTag = conn.prepareStatement(insertTagSql, Statement.RETURN_GENERATED_KEYS)) {
                                psInsertTag.setString(1, tagName);
                                psInsertTag.executeUpdate();
                                ResultSet rsTagKeys = psInsertTag.getGeneratedKeys();
                                if (rsTagKeys.next()) tagId = rsTagKeys.getInt(1);
                            }
                        }
                        if (tagId != -1) {
                            try (PreparedStatement psLink = conn.prepareStatement(linkTagSql)) {
                                psLink.setInt(1, post.getId());
                                psLink.setInt(2, tagId);
                                psLink.executeUpdate();
                            }
                        }
                    }
                }
                conn.commit(); return true;
            } catch (SQLException e) { conn.rollback(); e.printStackTrace(); }
            finally { conn.setAutoCommit(true); }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}