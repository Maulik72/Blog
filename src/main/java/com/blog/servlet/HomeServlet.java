package com.blog.servlet;

import com.blog.dao.CategoryDAO;
import com.blog.dao.PostDAO;
import com.blog.model.Category;
import com.blog.model.Post;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        int recordsPerPage = 6;

        // Parse Page Number
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) { page = 1; }
        }

        int offset = (page - 1) * recordsPerPage;

        PostDAO postDAO = new PostDAO();
        List<Post> posts;
        int noOfRecords;

        // NEW: Check if user is filtering by a category
        String categoryIdParam = request.getParameter("categoryId");
        if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
            try {
                int categoryId = Integer.parseInt(categoryIdParam);
                posts = postDAO.getPublishedPostsByCategoryAndPage(categoryId, offset, recordsPerPage);
                noOfRecords = postDAO.getTotalPublishedPostsCountByCategory(categoryId);
                request.setAttribute("activeCategoryId", categoryId); // To highlight the active filter
            } catch (NumberFormatException e) {
                // Invalid category ID, fallback to all posts
                posts = postDAO.getPublishedPostsByPage(offset, recordsPerPage);
                noOfRecords = postDAO.getTotalPublishedPostsCount();
            }
        } else {
            // Normal Homepage: Fetch all
            posts = postDAO.getPublishedPostsByPage(offset, recordsPerPage);
            noOfRecords = postDAO.getTotalPublishedPostsCount();
        }

        // Calculate pages
        int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);

        // NEW: Fetch all categories for the Sidebar
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> allCategories = categoryDAO.getAllCategories();

        request.setAttribute("categories", allCategories);
        request.setAttribute("postList", posts);
        request.setAttribute("noOfPages", noOfPages);
        request.setAttribute("currentPage", page);

        request.getRequestDispatcher("/home.jsp").forward(request, response);
    }
}