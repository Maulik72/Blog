package com.blog.servlet;

import com.blog.dao.ReactionDAO;
import com.blog.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/react-post")
public class ReactionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("loggedUser");
        String postIdParam = request.getParameter("postId");
        String action = request.getParameter("action"); // Will be "LIKE" or "DISLIKE"

        if (postIdParam != null && (action.equals("LIKE") || action.equals("DISLIKE"))) {
            try {
                int postId = Integer.parseInt(postIdParam);
                ReactionDAO reactionDAO = new ReactionDAO();
                reactionDAO.reactToPost(user.getId(), postId, action);

                // Redirect back to the post page
                response.sendRedirect("view-post?id=" + postId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.sendRedirect("home");
            }
        } else {
            response.sendRedirect("home");
        }
    }
}