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

@WebServlet("/edit-post")
public class EditPostServlet extends HttpServlet {

    // Shows the edit form pre-filled with data
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User loggedUser = (User) session.getAttribute("loggedUser");
        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int postId = Integer.parseInt(idParam);
                PostDAO postDAO = new PostDAO();

                // Fetch the post (passing the logged in user ID as the second param based on our recent updates)
                Post post = postDAO.getPostById(postId, loggedUser.getId());

                // Security check: Only the author can see the edit form
                if (post != null && post.getUserId() == loggedUser.getId()) {
                    request.setAttribute("post", post);
                    request.getRequestDispatcher("/edit-post.jsp").forward(request, response);
                } else {
                    response.sendRedirect("home?error=unauthorized");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect("home");
            }
        } else {
            response.sendRedirect("home");
        }
    }

    // Processes the form submission to update the database
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User loggedUser = (User) session.getAttribute("loggedUser");

        try {
            int postId = Integer.parseInt(request.getParameter("id"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            PostDAO postDAO = new PostDAO();
            boolean success = postDAO.updatePost(postId, loggedUser.getId(), title, content);

            if (success) {
                // If successful, send them to the updated post view
                response.sendRedirect("view-post?id=" + postId + "&msg=updated");
            } else {
                response.sendRedirect("home?error=update_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("home");
        }
    }
}