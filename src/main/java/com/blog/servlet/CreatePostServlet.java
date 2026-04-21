package com.blog.servlet;

import com.blog.dao.CategoryDAO;
import com.blog.dao.PostDAO;
import com.blog.model.Category;
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

@WebServlet("/create-post")
public class CreatePostServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login");
            return;
        }

        // Fetch categories to populate the dropdown menu
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        request.getRequestDispatcher("/create-post.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login");
            return;
        }

        User author = (User) session.getAttribute("loggedUser");

        // Get form data
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String categoryIdParam = request.getParameter("categoryId");
        String tagsInput = request.getParameter("tags");

        // Create Post object
        Post newPost = new Post();
        newPost.setUserId(author.getId());
        newPost.setTitle(title);
        newPost.setContent(content);

        // Parse Category ID safely
        if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
            try {
                newPost.setCategoryId(Integer.parseInt(categoryIdParam));
            } catch (NumberFormatException e) {
                newPost.setCategoryId(null);
            }
        }

        // Save to DB using the new tags method
        PostDAO postDAO = new PostDAO();
        if (postDAO.createPostWithTags(newPost, tagsInput)) {
            response.sendRedirect("home");
        } else {
            request.setAttribute("error", "Could not save post. Please try again.");
            // Re-fetch categories so the form doesn't crash on reload
            request.setAttribute("categories", new CategoryDAO().getAllCategories());
            request.getRequestDispatcher("/create-post.jsp").forward(request, response);
        }
    }
}