package com.blog.servlet;

import com.blog.dao.CommentDAO;
import com.blog.dao.PostDAO;
import com.blog.model.Comment;
import com.blog.model.Post;
import com.blog.model.User; // <-- Added this import
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // <-- Added this import
import java.io.IOException;
import java.util.List;

@WebServlet("/view-post")
public class ViewPostServlet extends HttpServlet {
    private PostDAO postDAO = new PostDAO();
    private CommentDAO commentDAO = new CommentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("home");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);

            // --- NEW LOGIC: Get the logged-in user's ID ---
            int loggedInUserId = 0; // Default to 0 for guests viewing the post
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("loggedUser") != null) {
                User user = (User) session.getAttribute("loggedUser");
                loggedInUserId = user.getId();
            }
            // ----------------------------------------------

            // Pass BOTH the postId and the loggedInUserId to the DAO
            Post post = postDAO.getPostById(id, loggedInUserId);

            if (post != null) {
                // --- NEW: Fetch Tags ---
                String tags = postDAO.getTagsForPost(id);
                request.setAttribute("tags", tags);

                // Fetch comments using the commentDAO instance
                List<Comment> comments = commentDAO.getCommentsByPost(id);

                // Set attributes for the JSP
                request.setAttribute("post", post);
                request.setAttribute("comments", comments);

                // Forward to JSP
                request.getRequestDispatcher("/post.jsp").forward(request, response);
            } else {
                response.sendRedirect("home"); // Redirect if the post doesn't exist
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("home"); // Catch invalid IDs like ?id=abc
        }
    }
}