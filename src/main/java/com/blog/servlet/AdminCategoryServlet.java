package com.blog.servlet;

import com.blog.dao.CategoryDAO;
import com.blog.model.Category;
import com.blog.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin-categories")
public class AdminCategoryServlet extends HttpServlet {

    // Helper method to check security
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

        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategories();

        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/admin-categories.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request)) { response.sendRedirect("home"); return; }

        String action = request.getParameter("action");
        CategoryDAO categoryDAO = new CategoryDAO();

        if ("add".equals(action)) {
            String name = request.getParameter("name");
            String description = request.getParameter("description");

            if (name != null && !name.trim().isEmpty()) {
                categoryDAO.addCategory(name, description);
            }
        }
        else if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                categoryDAO.deleteCategory(id);
            } catch (NumberFormatException e) {
                // Ignore bad IDs
            }
        }

        // Redirect back to the page to show the updated list
        response.sendRedirect("admin-categories");
    }
}