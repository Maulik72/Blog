package com.blog.servlet;

import com.blog.dao.PostDAO;
import com.blog.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/delete-post")
public class DeletePostServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // CHANGE 2: "user" -> "loggedUser"
        User currentUser = (User) session.getAttribute("loggedUser");

        // 2. Parse the Post ID
        String postIdParam = request.getParameter("id");
        if (postIdParam != null && !postIdParam.trim().isEmpty()) {
            try {
                int postId = Integer.parseInt(postIdParam);

                // 3. Execute Deletion
                PostDAO postDAO = new PostDAO();
                boolean success = postDAO.deletePost(postId, currentUser.getId());

                if (success) {
                    // Redirect to home with a success flag (optional, can be caught in JSP)
                    response.sendRedirect(request.getContextPath() + "/home?msg=deleted");
                } else {
                    // Redirect with an error if it failed (e.g., user didn't own the post)
                    response.sendRedirect(request.getContextPath() + "/home?error=unauthorized_or_not_found");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/home?error=invalid_id");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}