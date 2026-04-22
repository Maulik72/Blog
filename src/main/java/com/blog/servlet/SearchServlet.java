package com.blog.servlet;

import com.blog.dao.CategoryDAO;
import com.blog.dao.PostDAO;
import com.blog.dao.UserDAO;
import com.blog.model.Category;
import com.blog.model.Post;
import com.blog.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("q");

        if (query == null || query.trim().isEmpty()) {
            response.sendRedirect("home");
            return;
        }

        // Pagination setup
        int page = 1;
        int recordsPerPage = 6;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) { page = 1; }
        }
        int offset = (page - 1) * recordsPerPage;

        // Category Filter setup
        Integer categoryId = null;
        String categoryIdParam = request.getParameter("categoryId");
        if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryIdParam);
                request.setAttribute("activeCategoryId", categoryId);
            } catch (NumberFormatException e) { categoryId = null; }
        }

        PostDAO postDAO = new PostDAO();
        UserDAO userDAO = new UserDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        // 1. Fetch Paginated & Filtered Posts
        List<Post> postResults = postDAO.searchPostsPaginated(query.trim(), categoryId, offset, recordsPerPage);
        int noOfRecords = postDAO.getTotalSearchPostsCount(query.trim(), categoryId);
        int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);

        // 2. Fetch Users (We don't paginate users to keep the UI simple)
        List<User> userResults = userDAO.searchUsers(query.trim());

        // 3. Fetch Categories for the Dropdown menu
        List<Category> categories = categoryDAO.getAllCategories();

        // Attach everything to the request
        request.setAttribute("searchQuery", query);
        request.setAttribute("categories", categories);
        request.setAttribute("postResults", postResults);
        request.setAttribute("userResults", userResults);
        request.setAttribute("noOfPages", noOfPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPostCount", noOfRecords); // To show exact number found

        request.getRequestDispatcher("/search-results.jsp").forward(request, response);
    }
}