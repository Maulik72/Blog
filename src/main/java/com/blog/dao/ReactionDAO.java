package com.blog.dao;

import com.blog.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReactionDAO {

    public void reactToPost(int userId, int postId, String reactionType) {
        // Check if a reaction already exists
        String checkSql = "SELECT reaction_type FROM post_reactions WHERE user_id = ? AND post_id = ?";
        String insertSql = "INSERT INTO post_reactions (user_id, post_id, reaction_type) VALUES (?, ?, ?)";
        String updateSql = "UPDATE post_reactions SET reaction_type = ? WHERE user_id = ? AND post_id = ?";
        String deleteSql = "DELETE FROM post_reactions WHERE user_id = ? AND post_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, postId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String currentReaction = rs.getString("reaction_type");
                if (currentReaction.equals(reactionType)) {
                    // User clicked the same button again -> Remove the reaction (Unlike/Undislike)
                    try (PreparedStatement delStmt = conn.prepareStatement(deleteSql)) {
                        delStmt.setInt(1, userId);
                        delStmt.setInt(2, postId);
                        delStmt.executeUpdate();
                    }
                } else {
                    // User changed their mind (e.g., Like to Dislike) -> Update the reaction
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, reactionType);
                        updateStmt.setInt(2, userId);
                        updateStmt.setInt(3, postId);
                        updateStmt.executeUpdate();
                    }
                }
            } else {
                // No existing reaction -> Insert new reaction
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, postId);
                    insertStmt.setString(3, reactionType);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}