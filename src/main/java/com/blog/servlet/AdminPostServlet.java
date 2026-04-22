package com.blog.servlet;

import com.blog.dao.PostDAO;
import com.blog.model.Post;
import com.blog.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin-posts")
public class AdminPostServlet extends HttpServlet {

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            User user = (User) session.getAttribute("loggedUser");
            return "ADMIN".equals(user.getRole());
        }
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request)) { response.sendRedirect("home"); return; }

        PostDAO postDAO = new PostDAO();
        List<Post> allPosts = postDAO.getAllPostsForAdmin();

        request.setAttribute("postList", allPosts);
        request.getRequestDispatcher("/admin-posts.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request)) { response.sendRedirect("home"); return; }

        String action = request.getParameter("action");
        PostDAO postDAO = new PostDAO();

        try {
            int postId = Integer.parseInt(request.getParameter("postId"));

            if ("toggleStatus".equals(action)) {
                String currentStatus = request.getParameter("currentStatus");
                String newStatus = "PUBLISHED".equals(currentStatus) ? "DRAFT" : "PUBLISHED";
                postDAO.updatePostStatus(postId, newStatus);
            }
            else if ("delete".equals(action)) {
                postDAO.deletePost(postId);
            }
        } catch (NumberFormatException e) {
            // Ignore bad IDs
        }

        response.sendRedirect("admin-posts");
    }
}