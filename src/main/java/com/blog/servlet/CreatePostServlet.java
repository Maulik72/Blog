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

@WebServlet("/create-post")
public class CreatePostServlet extends HttpServlet {
    private PostDAO postDAO = new PostDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // SECURITY CHECK: Are they logged in?
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login"); // Kick to login
            return;
        }

        // If logged in, show the form
        request.getRequestDispatcher("/create-post.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // SECURITY CHECK
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login");
            return;
        }

        // Get the logged-in user from the session so we know who is writing the post
        User author = (User) session.getAttribute("loggedUser");

        // Get form data
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        // Create Post object
        Post newPost = new Post();
        newPost.setUserId(author.getId());
        newPost.setTitle(title);
        newPost.setContent(content);

        // Save to DB
        if (postDAO.createPost(newPost)) {
            // Success! Send them back to the homepage feed
            response.sendRedirect("home");
        } else {
            // Failed
            request.setAttribute("error", "Could not save post. Please try again.");
            request.getRequestDispatcher("/create-post.jsp").forward(request, response);
        }
    }
}